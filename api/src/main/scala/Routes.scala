import ServiceActor._
import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete

import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout

trait Routes extends JsonFormatSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  def serviceActor: ActorRef

  implicit lazy val timeout = Timeout(3.seconds)

  lazy val vehicleRoutes: Route = {
    pathPrefix("api" / "vehicles" / "list") {
      concat(
        pathEnd {
          concat(
            get {
              val vehicles: Future[TiledVehicles] = (serviceActor ? GetVehicles).mapTo[TiledVehicles]
              complete(StatusCodes.OK, vehicles)
            }
          )
        }
      )
    } ~
    path("api" / "vehicles" / "vehicle" / IntNumber / "lastPosition") { vehicleId =>
      get {
        val lastEntry: Future[Location] = (serviceActor ? GetVehicle(vehicleId.toString)).mapTo[Location]
        rejectEmptyResponse(complete(lastEntry))
      }
    } ~
    path("api" / "vehicles" / "vehiclesPerTile") {
      get {
        val vehilesPerTile: Future[VehiclesCountPerTile] = (serviceActor ? VehiclesPerTile).mapTo[VehiclesCountPerTile]
        complete(vehilesPerTile)
      }
    }
  }
}
