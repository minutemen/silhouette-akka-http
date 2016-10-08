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
package silhouette.akka.http.session

import java.net.{ URLDecoder, URLEncoder }
import silhouette.http.Cookie
import scala.util.Try

/**
 * Represents a session data
 *
 * @param sessionName the cookie name where session is store
 * @param data the data to store inside session
 */
case class Session(
  sessionName: String,
  data: Map[String, String]
)

object Session {
  private def urlEncode(s: String): String = URLEncoder.encode(s, "UTF-8")
  private def urlDecode(s: String): String = URLDecoder.decode(s, "UTF-8")

  /**
   * Extract [[Session]] from a [[Cookie]]
   * @param c The cookie where the session is store
   * @return a [[scala.util.Success]] with [[Session]] or [[scala.util.Failure]] if is not possible extract a [[Session]]
   */
  def fromCookie(c: Cookie): Try[Session] = Session.deserialize(c.value).map(v => Session(c.name, v.toMap))
  /**
   * Transform [[Session]] to a [[Cookie]]
   * @param s The session to transform
   * @return a [[Cookie]] with session serialized as value
   */
  def asCookie(s: Session): Cookie = Cookie(name = s.sessionName, value = Session.serialize(s.data))

  protected[session] def serialize(t: Map[String, String]): String = t.map(p => s"${urlEncode(p._1)}=${urlEncode(p._2)}").mkString("&")
  protected[session] def deserialize(r: String): Try[Map[String, String]] =
    Try(r.split("&").map(_.split("=", 2)).map(p => urlDecode(p(0)) -> urlDecode(p(1))).toSeq).map(_.toMap)

}
