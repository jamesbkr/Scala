
import akka.routing.Broadcast
import akka.cluster.Cluster
import akka.actor.Actor
import akka.routing.FromConfig
import akka.actor.Props
import akka.actor.RootActorPath
import akka.routing.ConsistentHashingPool
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.actor.{Actor, ActorRef,OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}



class FrontEndWorker extends Actor {	
	 val cluster = Cluster(context.system)

	
	val backend = context.actorSelection("akka://ClusterSystem/user/BackEndNodeProxy")
		  
		  def receive = {

			case msg: String =>  
				process(msg)
			case Flush => 
				backend ! Flush
		  }

def process(url: String) = {

	val title = url
		
		try{
			val tip = scala.io.Source.fromURL(url.trim).mkString
		}catch{case e: Throwable  => throw new Exception("Expect only integers");}

		val data = scala.io.Source.fromURL(url.trim).mkString

		for (word <- data.split("\\W+")) {
			if (word != "" && word != null) {
			if (Character.isUpperCase(word.charAt(0))) {
				context.actorSelection("akka://ClusterSystem/user/BackEndNodeProxy")  ! Word(word,title)
			}
			}
		}

  }

}


