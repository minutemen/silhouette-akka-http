package silhouette.akka.http

import akka.http.scaladsl.model.{HttpHeader, HttpResponse}
import akka.http.scaladsl.model.headers.{RawHeader, `Set-Cookie`}
import silhouette.akka.http.conversions.HttpCookieConversion._
import silhouette.akka.http.session.Session
import silhouette.http.{Cookie, ResponsePipeline}

case class AkkaHttpResponsePipeline(response: HttpResponse) extends ResponsePipeline[HttpResponse] {

  private def isCookie: HttpHeader => Boolean = _.is(`Set-Cookie`.lowercaseName)

  override def headers: Map[String, Seq[String]] = response.headers.foldLeft(Map(): Map[String, Seq[String]]) {
    case (acc, curr) if !isCookie(curr) =>
      val r = (curr.name(), curr.value() +: acc.getOrElse(curr.name(), Nil))
      acc + r
    case (acc, _) => acc
  }

  override def withHeaders(headers: (String, String)*): ResponsePipeline[HttpResponse] = {
    val newHeaders = headers.map(p => RawHeader(name = p._1, value = p._2))
    val newRes = response.copy(headers = response.headers.filter(h => !headers.exists(p => h.is(p._1.toLowerCase))) ++ newHeaders)
    AkkaHttpResponsePipeline(newRes)
  }

  override def cookies: Seq[Cookie] = {
    response.headers.collect {
      case `Set-Cookie`(cookie) => cookie
    }.map(c => httpCookieToCookie(c))
  }

  override def withCookies(cookies: Cookie*): ResponsePipeline[HttpResponse] = {
    val httpCookies = cookies.foldRight(List.empty[Cookie]) {
      case (c, Nil) => c :: Nil
      case (c, acc) if acc.exists(_.name == c.name) => acc
      case (c, acc) => c :: acc
    }
    val newCookie = (this.cookies.filter(c => !httpCookies.exists(_.name == c.name)) ++ httpCookies).map(cookieToHttpCookie)
    AkkaHttpResponsePipeline(response.withHeaders(response.headers.filterNot(isCookie) ++ newCookie.map(c => `Set-Cookie`(c))))
  }

  override def session: Map[String, String] = cookies.find(_.name == Session.name).flatMap(c => Session.fromCookie(c).toOption).getOrElse(Map())

  override def withSession(data: (String, String)*): ResponsePipeline[HttpResponse] = {
    val newSession = Session(Session.name, session ++ data.toMap)
    withCookies(Session.asCookie(newSession))
  }

  override def withoutSession(keys: String*): ResponsePipeline[HttpResponse] = {
    val newSession = Session(Session.name, session.filterNot(p => keys.contains(p._1)))
    withCookies(Session.asCookie(newSession))
  }

  override def unbox: HttpResponse = response

  override protected[silhouette] def touch: ResponsePipeline[HttpResponse] = {
    new AkkaHttpResponsePipeline(response) {
      override protected[silhouette] val touched = true
    }
  }
}
