import Dependencies._

ThisBuild / scalaVersion := "2.12.11"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"
val playVersion = "2.8.1"
val datastaxDriverVersion = "4.7.2"
lazy val root = (project in file("."))
  .settings(
    name := "scala-event-store",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.rabbitmq" % "amqp-client" % "5.8.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion,
    libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.20",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.3",
    libraryDependencies += "io.chrisdavenport" %% "log4cats-slf4j" % "1.1.1",
    libraryDependencies += "io.chrisdavenport" %% "log4cats-core" % "1.1.1",
    libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.3",
    libraryDependencies += "dev.profunktor" %% "fs2-rabbit" % "2.1.1",
    libraryDependencies += "dev.profunktor" %% "fs2-rabbit-circe" % "2.1.1",
    libraryDependencies += "com.datastax.oss" % "java-driver-core" % datastaxDriverVersion,
    libraryDependencies += "com.datastax.oss" % "java-driver-query-builder" % datastaxDriverVersion,
    libraryDependencies += "com.datastax.oss" % "java-driver-mapper-runtime" % datastaxDriverVersion
  )
