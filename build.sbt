name := """infoscreen"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.freemarker" % "freemarker" % "2.3.21",
  "org.apache.commons" % "commons-lang3" % "3.0",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "com.kenshoo" %% "metrics-play" % "2.3.0_0.1.8",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "com.mohiva" %% "play-html-compressor" % "0.3.1"
)

libraryDependencies += "org.eclipse.persistence" % "org.eclipse.persistence.jpa" % "2.5.2"



