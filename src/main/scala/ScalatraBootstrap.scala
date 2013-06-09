import com.github.cb372.phonehome._
import com.github.cb372.phonehome.listener.{MongoWriter, RecentEventsRecorder, LtsvPhoneHomeLogger}
import com.mongodb.casbah.{MongoClientURI, MongoClient}
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra._
import javax.servlet.ServletContext
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)

  val recentEventsRecorder = new RecentEventsRecorder(100)

  val mongoURI = MongoClientURI(sys.env.getOrElse("MONGOHQ_URL", "mongodb://localhost"))
  logger.info("MongoDB URI = {}", mongoURI)
  val mongoClient = MongoClient(mongoURI)
  val mongoDb = mongoClient("phonehome")
  val mongoWriter = new MongoWriter(mongoDb)

  val ltsvLogger = new LtsvPhoneHomeLogger

  val listeners  = Seq(
    recentEventsRecorder,
    ltsvLogger,
    mongoWriter
  )

  val authString = Some("not so secret")

  implicit val exContext = ExecutionContext.Implicits.global

  override def init(context: ServletContext) {
    context.mount(new StaticResourcesController, "/*")
    context.mount(new RecentEventsController(recentEventsRecorder), "/recent")
    context.mount(new PhoneHomeController(listeners, authString), "/ph/*")
  }

}
