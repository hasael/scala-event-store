import Dependencies._

ThisBuild / scalaVersion := "2.12.11"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"
val playVersion = "2.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "scala-event-store",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.rabbitmq" % "amqp-client" % "5.8.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion,
    libraryDependencies += "io.getquill" %% "quill-cassandra" % "3.5.1",
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.20",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3"
  )
