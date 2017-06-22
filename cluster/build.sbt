lazy val root = (project in file(".")).

settings(
  version := "1.0",
  scalaVersion := "2.11.8",
  scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  libraryDependencies ++= Seq(
			"com.typesafe.akka" %% "akka-actor" % "2.4.3",
			"com.typesafe.akka" %% "akka-cluster" % "2.4.4" ,
			"com.typesafe.akka" %% "akka-cluster-tools" % "2.4.4")
)

