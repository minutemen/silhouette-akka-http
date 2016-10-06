import Dependencies._

libraryDependencies ++= Seq(
  Library.silhouette,
  Library.akkaHttp,
  Library.Specs2.core % "test"
)

enablePlugins(Doc)
