package AccountService

import akka.cluster._

import com.typesafe.config.ConfigFactory
import akka.cluster.ClusterEvent.MemberUp
import akka.actor.{ Actor, ActorRef, ActorSystem, Props, RootActorPath }

class AccountService extends Actor {
	println(self.path)
  val cluster = Cluster(context.system)

  // subscribe to cluster changes, MemberUp
  // re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case Login(name, password) =>
      println(s"I'm a Account Registration with path: ${self} and I received add operation.")
      println(name + password)
     
    case MemberUp(member) =>
      if(member.hasRole("Controller")){
		 
        context.actorSelection(RootActorPath(member.address) / "user" / "Controller") !
          AccountServiceRegistration
      }
  }

}

object AccountService {
  def initiate(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.load().getConfig("AccountService"))

    val system = ActorSystem("ClusterSystem", config)

    val AccountService = system.actorOf(Props[AccountService], name = "AccountService")
  }
}

object AccountServiceApp extends App{
	
	AccountService.initiate(2245)
}
