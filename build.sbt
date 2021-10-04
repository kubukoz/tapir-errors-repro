val root = project
  .in(file("."))
  .settings(
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.6",
      "com.typesafe.akka" %% "akka-stream" % "2.6.16",
      "org.http4s" %% "http4s-ember-server" % "0.23.4",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.19.0-M10",
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.19.0-M10",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.19.0-M10",
    ),
    semanticdbEnabled := true,
  )
