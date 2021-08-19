ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.0.2-SNAPSHOT"
ThisBuild / organization := "io.github.waynewang12"
ThisBuild / crossScalaVersions := Seq("2.12.12", "2.13.6")

lazy val `play-quill` = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-jdbc"      % "2.8.8" % Provided,
      "io.getquill"       %% "quill-jdbc-zio" % "3.9.0"
    )
  )

lazy val example = (project in file("example"))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      "com.h2database"            % "h2"     % "1.4.200",
      "com.softwaremill.macwire" %% "macros" % "2.4.0" % "provided",
      jdbc,
      evolutions
    )
  )
  .dependsOn(`play-quill`)

ThisBuild / publishMavenStyle := true

credentials += Credentials(Path.userHome / ".sbt" / "credentials")

ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
