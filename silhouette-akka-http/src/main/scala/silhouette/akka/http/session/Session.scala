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
package silhouette.akka.http.session

import java.net.{ URLDecoder, URLEncoder }
import silhouette.http.Cookie
import scala.util.Try

/**
 * Represents session data.
 *
 * @param sessionName The cookie name where session is stored.
 * @param data The data to store inside the session.
 */
case class Session(
  sessionName: String,
  data: Map[String, String]
)

/**
 * The companion object.
 */
object Session {

  /**
   * Extract [[Session]] from a [[silhouette.http.Cookie]].
   *
   * @param cookie The cookie where the session is stored.
   * @return A [[scala.util.Success]] with [[Session]] or [[scala.util.Failure]] if is not possible
   *         extract a [[Session]].
   */
  def fromCookie(cookie: Cookie): Try[Session] = deserialize(cookie.value).map(v => Session(cookie.name, v))

  /**
   * Transform [[Session]] to a [[silhouette.http.Cookie]].
   *
   * @param session The session to transform.
   * @return A [[silhouette.http.Cookie]] with session serialized as value.
   */
  def asCookie(session: Session): Cookie = Cookie(name = session.sessionName, value = serialize(session.data))

  /**
   * Serializes the session data into an URL encoded string.
   *
   * @param data The session data to serialize.
   * @return An URL encoded string representation of the session data.
   */
  protected[session] def serialize(data: Map[String, String]): String =
    data.map(p => s"${urlEncode(p._1)}=${urlEncode(p._2)}").mkString("&")

  /**
   * Deserialize the URL encoded representation of the session data.
   *
   * @param data An URL encoded string representation of the session data.
   * @return The session data.
   */
  protected[session] def deserialize(data: String): Try[Map[String, String]] =
    Try(data.split("&").map(_.split("=", 2)).map(p => urlDecode(p(0)) -> urlDecode(p(1))).toSeq).map(_.toMap)

  private def urlEncode(s: String): String = URLEncoder.encode(s, "UTF-8")
  private def urlDecode(s: String): String = URLDecoder.decode(s, "UTF-8")
}
