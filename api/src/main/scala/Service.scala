import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

object ServiceActor {
  final case class GetVehicles()
  final case class GetVehicle(id: String)

  def props: Props = Props[ServiceActor]()
}

class ServiceActor extends Actor with ActorLogging {

  import ServiceActor._

  def receive: Receive = {
    case GetVehicles => {
      log.info("got request")
      val sender_ : ActorRef = sender()
      Database.vehiclesList() onComplete {
        case Success(result) => { sender_ ! TiledVehicles(result) }
        case Failure(t)      => {}
      }
    }
    case GetVehicle(id) => {
      val sender_ : ActorRef = sender()
      Database.getVehicleById(id) onComplete {
        case Success(result) => {
          val latitude  = result.get.latitude
          val longitude = result.get.longitude
          val location  = Location(longitude, latitude)
          sender_ ! location
        }
        case Failure(t) => {}
      }
    }
  }
}
