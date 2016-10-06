/**
 * Copyright 2015 Mohiva Organisation (license at mohiva dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import sbt._

object Dependencies {

  object Versions {
    val crossScala = Seq("2.11.8")
    val scalaVersion = crossScala.head
  }

  val resolvers = Seq(
    Resolver.sonatypeRepo("snapshots"),
    "Atlassian Releases" at "https://maven.atlassian.com/public/"
  )

  object Library {

    val silhouette = "com.mohiva" %% "silhouette" % "1.0.0-SNAPSHOT"

    object Specs2 {
      private val version = "3.6.5"
      val core = "org.specs2" %% "specs2-core" % version
      val matcherExtra = "org.specs2" %% "specs2-matcher-extra" % version
      val mock = "org.specs2" %% "specs2-mock" % version
    }
    val akkaHttp = "com.typesafe.akka" %% "akka-http-core" % "2.4.10"
  }
}
