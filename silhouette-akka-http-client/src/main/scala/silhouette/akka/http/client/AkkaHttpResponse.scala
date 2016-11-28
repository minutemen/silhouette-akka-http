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
package silhouette.akka.http.client

import akka.http.scaladsl.model.HttpResponse
import silhouette.akka.http.AkkaHttpResponsePipeline
import silhouette.http.client.{ Body, Response }

/**
 * The response implementation based on the [[akka.http.scaladsl.model.HttpResponse]].
 *
 * @param response The response this pipeline handles.
 */
case class AkkaHttpResponse(response: HttpResponse, body: Body) extends Response {

  val akkaHttpResponsePipeline = AkkaHttpResponsePipeline(response, sessionName = "session")

  override def header: Map[String, Seq[String]] = akkaHttpResponsePipeline.headers

  override def status: Int = akkaHttpResponsePipeline.response.status.intValue()

}
