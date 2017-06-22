
package scala

import akka.actor.{Actor, ActorRef}

class MapActor(reduceActors: List[ActorRef]) extends Actor {
  val numReducers = reduceActors.size
  def receive = {
    case File(title, content) =>   
        process(title,content)
    case Flush => 
      for (i <- 0 until numReducers) {
        reduceActors(i) ! Flush
      }
  }

  def process(title: String, content: String) = {
    for (word <- content.split("\\W+")) {
      if (word != "" && word != null) {
        if (Character.isUpperCase(word.charAt(0))) {
          var index = Math.abs((word.hashCode()) % numReducers)         
          reduceActors(index) !  Word(word, title)
        }
      }
    }
  }
}