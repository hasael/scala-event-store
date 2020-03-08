import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"
val playVersion = "2.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "scala-event-store",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.rabbitmq" % "amqp-client" % "5.8.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
