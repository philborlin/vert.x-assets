name := "vert.x-assets"
 
version := "0.1"
 
scalaVersion := "2.10.0"

resolvers += "Typesafe Repository (releases)" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "org.vert-x" % "vertx-lang-java" % "1.3.1.final"

libraryDependencies += "org.apache.directory.studio" % "org.apache.commons.io" % "2.1"