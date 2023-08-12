lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-by-examples",
    description := "Learn Scala 3 by examples",
    version := "0.1.0",
    scalaVersion := "3.3.0",
    scalacOptions ++= Seq("-deprecation"),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )