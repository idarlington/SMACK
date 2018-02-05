import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

object ServiceActor {
  final case class GetVehicles()
  final case class ListVehicles()

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
    case ListVehicles => {
      val sender_ = sender
      Database.get() onComplete {
        case Success(result) => { sender_ ! AllVehicles(result) }
        case Failure(t)      => {}
      }
    }
  }
}
//TODO Clean up naming
