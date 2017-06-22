import akka.routing.Broadcast
import akka.cluster.Cluster
import akka.actor.Actor
import akka.routing.FromConfig
import akka.actor.Props
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.actor.{Actor, ActorRef,OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}



class FrontEndNode extends Actor {
	var counter = 0
    val cluster = Cluster(context.system)
    val myMan = context.actorOf(FromConfig.props(Props[FrontEndWorker]),
    name = "frontEndSlave")
	
  def receive = {

    case msg: String =>
		myMan ! ConsistentHashableEnvelope(message = msg,hashKey = msg)
	case Flush => 
		myMan ! Broadcast(Flush)

  }
}
