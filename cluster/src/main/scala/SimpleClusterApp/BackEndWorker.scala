
import akka.cluster.Cluster
import akka.actor.Actor
import akka.routing.FromConfig
import akka.actor.Props
import scala.collection.mutable.HashMap

import akka.actor.{Actor, ActorRef, ActorSystem, ActorSelection, PoisonPill}

import com.typesafe.config.ConfigFactory




class BackEndWorker extends Actor {	
	val cluster = Cluster(context.system)
	var remainingMappers = 9
	var reduceMapCount = HashMap[String, Int]()
	var reduceMapTitles = HashMap[String, Array[String]]()
  
  def receive = {
    
    case Word(word, title) =>
      if (reduceMapCount.contains(word)) {
        
	            reduceMapCount += (word -> (reduceMapCount(word) + 1))
	             if(!(reduceMapTitles(word).contains(title) )){
	                    reduceMapTitles += (word -> (reduceMapTitles(word) :+ title) )
                }
      }
      else{
       
        val store=Array(title)
	      reduceMapCount += (word -> 1)
	      reduceMapTitles += (word -> store )
      }
      case Flush =>

			for (word <- reduceMapCount.keySet){
			println(word+ ":: " + reduceMapCount(word)+" :: "  +  reduceMapTitles(word).mkString(" ::::: "))
			}
			Thread.sleep(1000)
			context.system.terminate
		}
		
  }


