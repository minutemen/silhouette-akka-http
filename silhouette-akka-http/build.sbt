import Dependencies._

libraryDependencies ++= Seq(
  Library.Silhouette.core,
  Library.akkaHttp,
  Library.Specs2.core % "test"
)

enablePlugins(Doc)
