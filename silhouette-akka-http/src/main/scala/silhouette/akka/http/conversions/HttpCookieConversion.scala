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
package silhouette.akka.http.conversions

import akka.http.scaladsl.model.headers.HttpCookie
import silhouette.http.Cookie

/**
 * Utility object that convert a [[silhouette.http.Cookie]] to an [[akka.http.scaladsl.model.headers.HttpCookie]]
 * and vice versa.
 */
object HttpCookieConversion {

  /**
   * Converts a [[silhouette.http.Cookie]] to an [[akka.http.scaladsl.model.headers.HttpCookie]].
   */
  val cookieToHttpCookie: Cookie => HttpCookie = cookie => HttpCookie(
    name = cookie.name,
    value = cookie.value,
    expires = None,
    maxAge = cookie.maxAge.map(_.toLong),
    domain = cookie.domain,
    path = cookie.path,
    secure = cookie.secure,
    httpOnly = cookie.httpOnly,
    extension = None
  )

  /**
   * Converts an [[akka.http.scaladsl.model.headers.HttpCookie]] to an [[silhouette.http.Cookie]].
   */
  val httpCookieToCookie: HttpCookie => Cookie = httpCookie => Cookie(
    name = httpCookie.name,
    value = httpCookie.value,
    maxAge = httpCookie.maxAge.map(_.toInt),
    domain = httpCookie.domain,
    path = httpCookie.path,
    secure = httpCookie.secure,
    httpOnly = httpCookie.httpOnly
  )
}
