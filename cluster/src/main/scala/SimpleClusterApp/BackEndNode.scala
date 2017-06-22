

import akka.routing.Broadcast
import akka.cluster.Cluster
import akka.actor.Actor
import akka.routing.FromConfig
import akka.actor.Props
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope

class BackEndNode extends Actor {
	var counter = 0
    val cluster = Cluster(context.system)
	    val myMan = context.actorOf(FromConfig.props(Props[BackEndWorker]),
    name = "backEndSlave")
  
  def receive = {
    case Word(word, title) =>
		myMan ! ConsistentHashableEnvelope(message = Word(word,title),hashKey = word)
    case Flush => 
			myMan ! Broadcast(Flush)
     

  }
}
