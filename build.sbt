import Dependencies._
import sbt.Keys.libraryDependencies

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.kolotree"
ThisBuild / organizationName := "kolotree"

def commonSettings(projectName: String): Seq[sbt.Def.Setting[_]] = Seq(
  name := projectName,
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xfatal-warnings"
  )
)

lazy val root = (project in file("."))
  .settings(
    commonSettings("es-playground")
  )
  .aggregate(
    common,
    commandCommon,
    commandPorts,
    eventStoreDbAdapter
  )

lazy val common = project
  .in(new File("./common"))
  .settings(
    commonSettings("common")
  )

lazy val commandCommon = project
  .in(new File("./command-side/command-common"))
  .settings(
    commonSettings("command-common"),
    libraryDependencies += catsCore,
    libraryDependencies += scalaTest
  )
  .dependsOn(common)

lazy val commandPorts = project
  .in(new File("./command-side/command-ports"))
  .settings(
    commonSettings("command-ports"),
    libraryDependencies += catsCore
  )
  .dependsOn(commandCommon)

lazy val eventStoreDbAdapter = project
  .in(new File("./command-side/adapters/event-store-db-adapter"))
  .settings(
    commonSettings("event-store-db-adapter"),
    libraryDependencies += eventStoreDbClientJava,
    libraryDependencies += monix,
    libraryDependencies += json4s
  )
  .dependsOn(commandCommon, commandPorts)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
