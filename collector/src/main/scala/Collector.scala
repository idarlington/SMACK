import akka.actor.Props

object collector extends App {

  implicit val system = akka.actor.ActorSystem()

  val streamActor = system.actorOf(Props(new StreamActor()))

  import system.dispatcher

  import scala.concurrent.duration._
  import scala.language.postfixOps

  system.scheduler.schedule(0 milliseconds, 10 seconds, streamActor, "")
}
