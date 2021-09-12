/**
 * Licensed to the Minutemen Group under one or more contributor license
 * agreements. See the COPYRIGHT file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import sbt.Keys._
import sbt._

object Build extends Build {
  lazy val buildVersions = taskKey[Unit]("Show some build versions")

  val silhouetteAkkaHttp = Project(
    id = "silhouette-akka-http",
    base = file("silhouette-akka-http")
  )

  val silhouetteAkkaHttpClient = Project(
    id = "silhouette-akka-http-client",
    base = file("silhouette-akka-http-client"),
    dependencies = Seq(silhouetteAkkaHttp)
  )

  val root = Project(
    id = "root",
    base = file("."),
    aggregate = Seq(
      silhouetteAkkaHttp,
      silhouetteAkkaHttpClient
    ),
    settings = Defaults.coreDefaultSettings ++
      APIDoc.settings ++
      Seq(
        publish := {},
        buildVersions := {
          // scalastyle:off println
          println(s"PROJECT_VERSION ${version.value}")
          println(s"SCALA_VERSION ${scalaVersion.value}")
          // scalastyle:on println
        }
      )
  )
}
