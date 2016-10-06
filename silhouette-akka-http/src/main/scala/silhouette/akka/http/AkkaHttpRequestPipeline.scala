package silhouette.akka.http

import akka.http.scaladsl.model.{HttpHeader, HttpRequest, Uri}
import akka.http.scaladsl.model.headers.RawHeader
import silhouette.http.{Cookie, RequestBodyExtractor, RequestPipeline}
import silhouette.akka.http.conversions.HttpCookieConversion._
import silhouette.akka.http.session.Session

case class AkkaHttpRequestPipeline(request: HttpRequest) extends RequestPipeline[HttpRequest] {
  private def isCookie: HttpHeader => Boolean = _.is(akka.http.scaladsl.model.headers.Cookie.lowercaseName)

  override def headers: Map[String, Seq[String]] = request.headers.foldLeft(Map(): Map[String, Seq[String]]) {
    case (acc, curr) if !isCookie(curr) =>
      val r = (curr.name(), curr.value() +: acc.getOrElse(curr.name(), Nil))
      acc + r
    case (acc, _) => acc
  }

  override def withHeaders(headers: (String, String)*): RequestPipeline[HttpRequest] = {
    val newHeaders = headers.map(p => RawHeader(name = p._1, value = p._2))
    val newReq = request.copy(headers = request.headers.filter(h => !headers.exists(p => h.is(p._1.toLowerCase))) ++ newHeaders)
    AkkaHttpRequestPipeline(newReq)
  }

  override def cookies: Seq[Cookie] = request.cookies.map(c => httpCookieToCookie(c.toCookie()))

  override def withCookies(cookies: Cookie*): RequestPipeline[HttpRequest] = {
    val httpCookies = cookies.foldRight(List.empty[Cookie]) {
      case (c, Nil) => c :: Nil
      case (c, acc) if acc.exists(_.name == c.name) => acc
      case (c, acc) => c :: acc
    }
    val newCookie = request.cookies.filter(c => !httpCookies.exists(_.name == c.name)).map(_.toCookie()) ++ httpCookies.map(cookieToHttpCookie)
    AkkaHttpRequestPipeline(request.withHeaders(request.headers.filterNot(isCookie) ++ newCookie.map(c => akka.http.scaladsl.model.headers.`Cookie`(c.pair()))))
  }

  override def session: Map[String, String] = cookies.find(_.name == Session.name).flatMap(c => Session.fromCookie(c).toOption).getOrElse(Map())

  override def withSession(data: (String, String)*): RequestPipeline[HttpRequest] = {
    val newSession = Session(Session.name, session ++ data.toMap)
    withCookies(Session.asCookie(newSession))
  }

  override def rawQueryString: String = request.uri.rawQueryString.getOrElse("")

  override def queryParams: Map[String, Seq[String]] = request.uri.query().toMultiMap

  override def withQueryParams(params: (String, String)*): RequestPipeline[HttpRequest] = {
    val newQueryParams = request.uri.query().filter(p => !params.exists(_._1 == p._1)).toList ++ params
    AkkaHttpRequestPipeline(request.withUri(request.uri.withQuery(Uri.Query(newQueryParams: _*))))
  }

  override def unbox: HttpRequest = request

  override val bodyExtractor: RequestBodyExtractor[HttpRequest] = new RequestBodyExtractor[HttpRequest] {

    /**
      * Extracts a value from Json body.
      *
      * @param name The name of the value to extract.
      * @return [[BodyValue]]
      */
    override def fromJson(name: String): BodyValue = None

    /**
      * Extracts a value from Xml body.
      *
      * @param name The name of the value to extract.
      * @return [[BodyValue]]
      */
    override def fromXml(name: String): BodyValue = None

    /**
      * Extracts a value from form url encoded body.
      *
      * @param name The name of the value to extract.
      * @return [[BodyValue]]
      */
    override def fromFormUrlEncoded(name: String): BodyValue = None
  }
}
