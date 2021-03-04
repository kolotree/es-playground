import sbt._

object Dependencies {
  lazy val catsCore = "org.typelevel" %% "cats-core" % "2.4.2"

  lazy val eventStoreDbClientJava = "com.geteventstore" %% "eventstore-client" % "7.3.1"

  lazy val monix = "io.monix" %% "monix" % "3.3.0"

  lazy val json4s = "org.json4s" %% "json4s-native" % "3.6.11"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.5" % "test"
}
