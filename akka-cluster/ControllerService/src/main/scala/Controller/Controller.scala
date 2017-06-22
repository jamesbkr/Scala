package Controller
import Messages._
import akka.cluster._
import scala.util.Random
import com.typesafe.config.ConfigFactory
import akka.actor.{ Actor, ActorRef, ActorSystem, Props, Terminated }


class Controller extends Actor {
	println(self.path)
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
		println("CONNECTED TO ACCOUNTING SERVICE!!!!")
		context watch(sender())
     case TradeServiceRegistration if !(trade.contains(sender())) =>
        trade = trade :+ sender()
        println("CONNECTED TO TRADING  SERVICE!!!!")
        context watch(sender())
        
     case _ => println("GOT SOMETHING ELSEEEEEEEEE")
  }

}


object Controller {

  private var _controller: ActorRef = _ 

  def initiate() = {
   val config = ConfigFactory.load().getConfig("Controller")

    val system = ActorSystem("ClusterSystem", config)

    _controller = system.actorOf(Props[Controller], name = "Controller")
    
  }

  def getController = _controller
}

object ControllerService extends App{
	Controller.initiate()
	
}
