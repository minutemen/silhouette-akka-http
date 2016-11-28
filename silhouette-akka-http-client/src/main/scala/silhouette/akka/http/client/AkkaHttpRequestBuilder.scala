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

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import silhouette.akka.http.AkkaHttpRequestPipeline
import silhouette.http.client.{ Body, RequestBuilder, Response, ContentType => SContentType }
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.Codec

/**
 * The request implementation based on the `akka.http.scaladsl.model.HttpRequest`.
 *
 * @param request The request this pipeline handles.
 */
case class AkkaHttpRequestBuilder(
  request: HttpRequest = HttpRequest()
)(implicit system: ActorSystem, fm: Materializer) extends RequestBuilder {

  override type Self = AkkaHttpRequestBuilder

  implicit val ec: ExecutionContext = system.dispatcher

  private val akkaHttpRequestPipeline = AkkaHttpRequestPipeline(request, sessionName = "session")

  override def withUrl(url: String): Self = {
    copy(request = akkaHttpRequestPipeline.request.withUri(url))
  }

  override def withMethod(method: String): Self = {
    copy(request = akkaHttpRequestPipeline.request.withMethod(HttpMethods.getForKey(method).get))
  }

  override def withHeaders(headers: (String, String)*): Self = {
    copy(request = akkaHttpRequestPipeline.withHeaders(headers: _*).request)
  }

  override def withQueryParams(params: (String, String)*): Self = {
    copy(request = akkaHttpRequestPipeline.withQueryParams(params: _*).request)
  }

  override def withBody(body: Body): Self = {
    import scala.collection.JavaConverters._
    val mediaType = MediaType.parse(body.contentType.value).getOrElse(MediaTypes.`application/json`)
    val charset = HttpCharset(body.codec.charSet.name())(body.codec.charSet.aliases().asScala.toList)
    val contentType = ContentType(mediaType, () => charset)
    val entity = HttpEntity(contentType, body.data)
    copy(request = akkaHttpRequestPipeline.request.withEntity(entity))
  }

  override def execute: Future[Response] = {
    Http().singleRequest(request).flatMap { response =>
      // TODO: get timeout from configuration
      response.entity.toStrict(10.seconds).map { entity =>
        val contentType = SContentType(entity.contentType.value)
        val codec = entity.contentType.charsetOption.map(c => Codec(c.value)).getOrElse(Body.DefaultCodec)
        AkkaHttpResponse(response, Body(contentType, codec, entity.data.toArray))
      }
    }
  }
}
