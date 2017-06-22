package cluster

import akka.cluster._

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }


object ClusterApp extends App {

  //initiate frontend node
  Frontend.initiate()

  //initiate three nodes from backend
  AccountService.initiate(2552)

  

  //Backend.initiate(2561)

  Thread.sleep(2000)
  
  Frontend.getFrontend ! Login("hello", "THere")
  Frontend.getFrontend ! Trade("XYS", "BUY")

}
