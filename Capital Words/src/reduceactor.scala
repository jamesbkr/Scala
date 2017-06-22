
package scala

import scala.collection.mutable.HashMap

import akka.actor.{Actor, ActorRef}
import com.typesafe.config.ConfigFactory

class ReduceActor extends Actor {
  var remainingMappers = ConfigFactory.load.getInt("number-mappers")
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
      remainingMappers -= 1
      if (remainingMappers == 0) {
        for (word <- reduceMapCount.keySet){
        println(word+ ":: " + reduceMapCount(word)+" :: "  +  reduceMapTitles(word).mkString(" ::::: "))
        }
        context.parent ! Done
      }
  }
}