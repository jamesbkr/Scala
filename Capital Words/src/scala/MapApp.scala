
package scala

import scala.io.Source
import akka.actor.{Actor,ActorRef,Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.apache.commons.validator.routines.UrlValidator
import java.nio.file.{Paths,Files}

object MapApp extends App{
  val schemes=Array("https","http")
  var A : Map[String,String] = Map()
  val fileNameString = ConfigFactory.load.getString("input")
  val fileNameList = fileNameString.split(",")
  val c = new UrlValidator(schemes);
  
  
  for (x <- fileNameList){
    
    if (c.isValid(x.trim))
    {
      A+=(x.trim -> scala.io.Source.fromURL(x.trim).mkString)
    }
    else if(Files.exists(Paths.get("C:/Users/james/workspace/mapReduceCapitalWords/inputs/"+x.trim))){
      A+= (x.trim -> scala.io.Source.fromFile("C:/Users/james/workspace/mapReduceCapitalWords/inputs/"+x.trim).mkString)
    }
    else{
      print("the file or url: " +x+ " is not valid.")
    }
  }
  
 val system = ActorSystem("mapSystem")
 val master = system.actorOf(Props[MasterActor],name = "master")
  
  A.foreach{case (key,value) => master ! File(key,value)}
   Thread.sleep(500)
  master ! Flush
 // val lines = Source.fromFile("/inputs/input.txt").getLines.toList
  
  
  
  
}