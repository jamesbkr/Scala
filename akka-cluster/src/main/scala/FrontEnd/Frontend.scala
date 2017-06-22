package cluster

import akka.cluster._
import scala.util.Random
import com.typesafe.config.ConfigFactory
import akka.actor.{ Actor, ActorRef, ActorSystem, Props, Terminated }


class Frontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]
  var accounting	= IndexedSeq.empty[ActorRef]
  var trade = IndexedSeq.empty[ActorRef]
  def receive = {
    
    //trading Service
    case Trade if trade.isEmpty =>
      println("Trade Service unavailable, cluster doesn't have backend node.")
    case tradeOp: Trade =>
      println("Frontend: I'll forward add operation to trade node to handle it.")
      trade(Random.nextInt(trade.size)) forward tradeOp
    
    // accounting Service
    case Login if accounting.isEmpty =>
		println("CANNOT LOGIN AT THIS TIME")
	case loginOp: Login =>
		accounting(Random.nextInt(accounting.size)) forward loginOp
   
   //node terminated
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
    
   //registrations 
     case AccountServiceRegistration if !(accounting.contains(sender())) =>
		accounting = accounting :+ sender()
		context watch(sender())
     case TradeServiceRegistration if !(trade.contains(sender())) =>
        trade = trade :+ sender()
        context watch(sender())
  }

}


object Frontend {

  private var _frontend: ActorRef = _ 

  def initiate() = {
   val config = ConfigFactory.load().getConfig("Frontend")

    val system = ActorSystem("ClusterSystem", config)

    _frontend = system.actorOf(Props[Frontend], name = "frontend")
  }

  def getFrontend = _frontend
}
