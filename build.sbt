import Dependencies._

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "es-playground"
  )
  .aggregate(
    common,
    commandCommon,
    commandPorts
  )

lazy val common = project
  .in(new File("./common"))
  .settings(
    name := "common"
  )

lazy val commandCommon = project
  .in(new File("./command-side/command-common"))
  .settings(
    name := "command-common",
    libraryDependencies += catsCore,
    libraryDependencies += scalaTest
  )
  .dependsOn(common)

lazy val commandPorts = project
  .in(new File("./command-side/command-ports"))
  .settings(
    name := "command-ports"
  )
  .dependsOn(commandCommon)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
