

import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.PoisonPill
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.cluster.singleton.ClusterSingletonProxy
import akka.cluster.singleton.ClusterSingletonProxySettings


object SimpleClusterApp {
	
	
  def main(args: Array[String]): Unit = {
    val fileNameString = ConfigFactory.load.getString("input")
    val fileNameList = fileNameString.split(",")
    
    
    
    if (args.isEmpty)
      startup(Seq("2551", "2552", "0"))
    else
      startup(args)
    
    
    
    val system = ActorSystem("ClusterSystem")
	val user = system.actorOf(Props(classOf[ClientNode],"user/FrontEndNodeProxy"), "client")

    Thread.sleep(10000)

     for (x <- fileNameList){
     user ! x 
		}
    Thread.sleep(10000)
    user ! Flush




  }
  
  
  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>

      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]")).withFallback(ConfigFactory.load())


      val system = ActorSystem("ClusterSystem", config)
	   
	  /////////////FRONT END ROUTER
	  
      system.actorOf(ClusterSingletonManager.props(
        singletonProps = Props[FrontEndNode],
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system).withRole("compute")),
        name = "FrontEndNode")

      system.actorOf(ClusterSingletonProxy.props(singletonManagerPath = "user/FrontEndNode",
        settings = ClusterSingletonProxySettings(system)),
        name = "FrontEndNodeProxy")
 
	  
	  ////////////////BACK END ROUTER

      system.actorOf(ClusterSingletonManager.props(
        singletonProps = Props[BackEndNode],
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system).withRole("compute")),
        name = "BackEndNode")

      system.actorOf(ClusterSingletonProxy.props(singletonManagerPath = "user/BackEndNode",
        settings = ClusterSingletonProxySettings(system)),
        name = "BackEndNodeProxy")
  
	  
	  
	  
	  
	  
    }
  }

}


