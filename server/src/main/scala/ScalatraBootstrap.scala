import com.github.cb372.phonehome._
import com.github.cb372.phonehome.listener.LtsvLogger
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    val listeners  = Seq(
      new LtsvLogger
    )

    val authString = Some("not so secret")

    context.mount(new StaticResourcesController, "/*")
    context.mount(new PhoneHomeController(listeners, authString), "/ph/*")
  }
}
