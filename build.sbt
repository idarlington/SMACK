name := "acdc-assesment"
organization in ThisBuild := "com.lunatech"
scalaVersion in ThisBuild := "2.11.8"

// PROJECTS

lazy val akkaVersion = "2.5.9"

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    collector,
    api,
    aggregator
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val collector = project
  .settings(
    name := "collector",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.monocleCore,
      dependencies.monocleMacro
    )
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(
    common
  )

lazy val api = project
  .settings(
    name := "api",
    dockerExposedPorts := Seq(8081),
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.pureconfig
    )
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(
    common
  )

lazy val aggregator = project
  .settings(
    name := "aggregator",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % dependencies.sparkVersion,
      "org.apache.spark" % "spark-streaming_2.11"            % dependencies.sparkVersion,
      "org.apache.spark" %% "spark-sql"                      % dependencies.sparkVersion
    )
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .dependsOn(
    common
  )

lazy val dependencies =
  new {
    val logbackV        = "1.2.3"
    val logstashV       = "4.11"
    val scalaLoggingV   = "3.7.2"
    val slf4jV          = "1.7.25"
    val typesafeConfigV = "1.3.1"
    val pureconfigV     = "0.8.0"
    val monocleV        = "1.4.0"
    val akkaV           = "2.5.6"
    val scalatestV      = "3.0.4"
    val scalacheckV     = "1.13.5"
    val kafkaStreamingV = "2.2.1"
    val sparkVersion    = "2.2.0"

    val logback        = "ch.qos.logback"             % "logback-classic"                 % logbackV
    val logstash       = "net.logstash.logback"       % "logstash-logback-encoder"        % logstashV
    val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"                  % scalaLoggingV
    val slf4j          = "org.slf4j"                  % "jcl-over-slf4j"                  % slf4jV
    val typesafeConfig = "com.typesafe"               % "config"                          % typesafeConfigV
    val akka           = "com.typesafe.akka"          %% "akka-stream"                    % akkaV
    val monocleCore    = "com.github.julien-truffaut" %% "monocle-core"                   % monocleV
    val monocleMacro   = "com.github.julien-truffaut" %% "monocle-macro"                  % monocleV
    val pureconfig     = "com.github.pureconfig"      %% "pureconfig"                     % pureconfigV
    val scalatest      = "org.scalatest"              %% "scalatest"                      % scalatestV
    val scalacheck     = "org.scalacheck"             %% "scalacheck"                     % scalacheckV
    val sparkStreaming = "org.apache.spark"           % "spark-streaming-kafka-0-10_2.11" % kafkaStreamingV
  }

lazy val commonDependencies = Seq(
  "com.typesafe.akka"      %% "akka-actor"              % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream"             % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"               % "10.0.10",
  "org.apache.kafka"       %% "kafka"                   % "0.11.0.2",
  "com.typesafe.akka"      %% "akka-http-spray-json"    % "10.1.0-RC1",
  "com.outworkers"         %% "phantom-dsl"             % "2.20.0",
  "com.typesafe"           % "config"                   % "1.3.1",
  "com.datastax.cassandra" % "cassandra-driver-core"    % "3.4.0",
  "com.datastax.cassandra" % "cassandra-driver-extras"  % "3.4.0",
  "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.4.0"
) map (_.exclude("org.slf4j", "log4j-over-slf4j"))

// SETTINGS

lazy val settings =
commonSettings ++
wartremoverSettings ++
scalafmtSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    "apache-snapshots" at "http://repository.apache.org/snapshots/",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val wartremoverSettings = Seq(
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true,
    scalafmtVersion := "1.2.0"
  )

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
