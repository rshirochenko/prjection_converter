lazy val root = (project in file(".")).
  settings(
    name := "Test-task",
    version := "1.0",
    scalaVersion := "2.11.6",
    mainClass in Compile := Some("http.MyApplication")
  )

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.locationtech.geotrellis" %% "geotrellis-raster" % "1.0.0",
  // akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2" % "test",
  // akka http
  "com.typesafe.akka" %% "akka-http-core" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.2" % "test",
  // the next one add only if you need Spray JSON support
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"

)
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}