

import akka.cluster.Cluster
import akka.actor.Actor

import scala.concurrent.duration._
import java.util.concurrent.ThreadLocalRandom
import com.typesafe.config.ConfigFactory
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Address
import akka.actor.PoisonPill
import akka.actor.Props
import akka.actor.RelativeActorPath
import akka.actor.RootActorPath
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.MemberStatus

class ClientNode(servicePath : String) extends Actor {
import context.dispatcher


override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])
  }
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
   
  }
	var counter = 0
    val cluster = Cluster(context.system)
    var nodes = Set.empty[Address]
	
	val servicePathElements = servicePath match {
    case RelativeActorPath(elements) => elements
    case _ => throw new IllegalArgumentException(
      "servicePath [%s] is not a valid relative actor path!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" format servicePath)
  }
  
  
  def receive = {

    case msg:String if nodes.nonEmpty =>

      val address = nodes.toIndexedSeq(ThreadLocalRandom.current.nextInt(nodes.size))  
      val service = context.actorSelection(RootActorPath(address) / servicePathElements)
		service ! msg

    case msg:String => 
		println("NNNNNNNNNNOOOOOOOOOOODDDDDDDDDEEEEEEEEEEESSSSSSSSSSSSSSS")
    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.status == MemberStatus.Up => m.address
      }
    case MemberUp(m) 						        => nodes += m.address
    case other: MemberEvent                         => nodes -= other.member.address
    case UnreachableMember(m)                       => nodes -= m.address
    case ReachableMember(m) 						 => nodes += m.address
    case Flush	=>
          val address = nodes.toIndexedSeq(ThreadLocalRandom.current.nextInt(nodes.size))  
			val service = context.actorSelection(RootActorPath(address) / servicePathElements)
		
			service !  Flush
			val address2 = nodes.toIndexedSeq(ThreadLocalRandom.current.nextInt(nodes.size))  
			val service2 = context.actorSelection(RootActorPath(address) / servicePathElements)
		
			service2 !  Flush
			

  }
}
