import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer

import Directives._
import AppConfiguration._

object Server extends App with Routes {

  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .handle {
        case MissingCookieRejection(cookieName) =>
          complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
      }
      .handle {
        case AuthorizationFailedRejection =>
          complete((Forbidden, "You're out of your depth!"))
      }
      .handle {
        case ValidationRejection(msg, _) =>
          complete((InternalServerError, "That wasn't valid! " + msg))
      }
      .handleAll[MethodRejection] { methodRejections =>
        val names = methodRejections.map(_.supported.name)
        complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
      .handleNotFound { complete((NotFound, "Not here!")) }
      .result()

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: ArithmeticException =>
        extractUri { uri =>
          println(s"Request to $uri could not be handled normally")
          complete(HttpResponse(InternalServerError, entity = "Bad numbers, bad result!!!"))
        }

      case _: java.util.NoSuchElementException =>
        extractUri { uri =>
          println(s"Request to $uri could not be handled normally")
          complete(HttpResponse(BadRequest, entity = "Bad numbers, bad result!!!"))
        }
      case _: akka.pattern.AskTimeoutException => {
        complete(HttpResponse(BadRequest, entity = "Wrong Request"))
      }
    }

  implicit val system: ActorSystem             = ActorSystem("apiHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val serviceActor: ActorRef = system.actorOf(ServiceActor.props, "serviceActor")

  lazy val routes: Route = vehicleRoutes

  Http().bindAndHandle(routes, apiHost, apiPort)

  println(s"Server online at http://0.0.0.0:8081/")

  Await.result(system.whenTerminated, Duration.Inf)
}
