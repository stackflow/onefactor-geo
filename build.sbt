import sbt.Keys.test

lazy val akkaV = "2.5.17"
lazy val akkaHttpV = "10.1.5"
lazy val circeV = "0.10.0"

lazy val dependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,

  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
  "io.circe" %% "circe-core" % circeV,
  "io.circe" %% "circe-generic" % circeV,
  "io.circe" %% "circe-parser" % circeV,

  "org.scalatest" %% "scalatest" % "3.0.5"
)

lazy val commonSettings = Seq(
  version := "1.0.0",
  organization := "onefactor",
  scalaVersion := "2.12.6",
  test in assembly := {}
)

lazy val geo = (project in file("."))
  .settings(
    commonSettings,
    assemblyJarName in assembly := "onefactor-geo.jar",
    assemblyOutputPath in assembly := baseDirectory.value / (assemblyJarName in assembly).value,
    mainClass in assembly := Some("onefactor.GeoMain"),
    libraryDependencies := dependencies
  )

lazy val producer = (project in file("producer"))
  .settings(
    commonSettings
  )
