import com.github.cb372.phonehome._
import com.github.cb372.phonehome.listener.{RecentEventsRecorder, LtsvPhoneHomeLogger}
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra._
import javax.servlet.ServletContext
import scala.concurrent.ExecutionContext

class ScalatraBootstrap extends LifeCycle {

  val recentEventsRecorder = new RecentEventsRecorder(100)
  val listeners  = Seq(
    new LtsvPhoneHomeLogger,
    recentEventsRecorder
  )

  val authString = Some("not so secret")

  implicit val exContext = ExecutionContext.Implicits.global

  override def init(context: ServletContext) {
    context.mount(new StaticResourcesController, "/*")
    context.mount(new RecentEventsController(recentEventsRecorder), "/recent")
    context.mount(new PhoneHomeController(listeners, authString), "/ph/*")
    context.mount(new HealthCheckController, "/health")
  }

}
