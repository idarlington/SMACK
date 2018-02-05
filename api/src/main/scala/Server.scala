import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

object Main extends App with Routes {

  implicit val system: ActorSystem             = ActorSystem("apiHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val serviceActor: ActorRef = system.actorOf(ServiceActor.props, "serviceActor")

  lazy val routes: Route = vehicleRoutes

  Http().bindAndHandle(routes, "localhost", 8081)

  println(s"Server online at http://localhost:8081/")

  Await.result(system.whenTerminated, Duration.Inf)
}
