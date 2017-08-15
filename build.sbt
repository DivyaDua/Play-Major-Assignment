name := """KIP-PLAY"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

javaOptions in Test += "-Dconfig.file=conf/test.conf"

coverageExcludedPackages := "<empty>;Reverse.*;router\\.*"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "42.1.4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test,
  "com.h2database" % "h2" % "1.4.188",
  "org.mockito" % "mockito-core" % "2.8.47" % "test",
  "org.mindrot" % "jbcrypt" % "0.4",
  specs2 % Test,
  evolutions
)

