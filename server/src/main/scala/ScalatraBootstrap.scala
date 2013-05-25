import com.github.cb372.phonehome._
import com.github.cb372.phonehome.listener.LtsvLogger
import org.scalatra._
import javax.servlet.ServletContext
import scala.concurrent.ExecutionContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    val listeners  = Seq(
      new LtsvLogger
    )

    val authString = Some("not so secret")

    implicit val exContext = ExecutionContext.Implicits.global

    context.mount(new StaticResourcesController, "/*")
    context.mount(new PhoneHomeController(listeners, authString), "/ph/*")
  }
}
