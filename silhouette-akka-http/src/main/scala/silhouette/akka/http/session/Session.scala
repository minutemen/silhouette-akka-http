package silhouette.akka.http.session

import java.net.{URLDecoder, URLEncoder}
import silhouette.http.Cookie
import scala.util.Try

case class Session(
  sessionName: String,
  data: Map[String, String]
)

object Session {
  lazy val name = "session"
  private def urlEncode(s: String): String = URLEncoder.encode(s, "UTF-8")
  private def urlDecode(s: String): String = URLDecoder.decode(s, "UTF-8")

  def fromCookie(c: Cookie) = Session.deserialize(c.value).map(_.toMap)
  def asCookie(s: Session) = Cookie(name = s.sessionName, value = Session.serialize(s.data))

  def serialize(t: Map[String, String]): String = t.map(p => s"${urlEncode(p._1)}=${urlEncode(p._2)}").mkString("&")
  def deserialize(r: String): Try[Map[String, String]] =
    Try(r.split("&").map(_.split("=", 2)).map(p => urlDecode(p(0)) -> urlDecode(p(1))).toSeq).map(_.toMap)

}
