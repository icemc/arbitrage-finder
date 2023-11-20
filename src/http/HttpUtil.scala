package src.http

import sttp.client3.*
import sttp.capabilities.*
import sttp.model.*
import scala.util.{Right, Try}
import io.circe.*
import io.circe.syntax.*
import java.io.InputStream
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

trait HttpUtil {
    import HttpUtil._

    protected def backend: SttpBackend[Future, WebSockets]

    protected def executionContext: ExecutionContext

    given ex: ExecutionContext = executionContext

  /**
   * Send get request to remote url. JSON body will be marshalled to type T
   * @param url remote url
   * @param headers headers to pass to request
   * @tparam T type to marshall json body to
   * @return T
   */
    protected def getJson[T: Decoder](url: String)
                                   (using headers: Seq[Header] = Seq(Header(HeaderNames.Accept, "application/json"))

                                   ): Future[T] =

      val request = basicRequest
        .headers(headers: _*)
        .acceptEncoding("application/json")
        .get(Uri.unsafeParse(url))

      retry(request.send(backend).map(_.body.flatMap(_.asJson.as[T])).flatMap {
        case Left(_) => Future.failed(HttpRequestError())
        case Right(value) => Future.successful(value)
      }, 5)
    end getJson


  /**
   * Send get request to remote url. Return body as plain text
   * @param url remote url
   * @param headers headers to be passed to request
   * @return body as string
   */
    protected def get(url: String)
                   (using headers: Seq[Header] = Seq(Header(HeaderNames.Accept, "text/plain"))

                   ): Future[String] =

      val request = basicRequest
        .headers(headers: _*)
        .get(Uri.unsafeParse(url))

      retry(request.send(backend).map(_.body).flatMap {
        case Left(_) =>
          Future.failed(HttpRequestError())
        case Right(value) =>
          Future.successful(value)
      }, 5)
    end get

    private def retry[A](future: => Future[A], remainingAttempts: Int): Future[A] =
      if remainingAttempts <= 0 then future
      else future.recoverWith {
        case _ => retry(future, remainingAttempts - 1)
      }
    end retry


}

object HttpUtil {
  trait HttpError extends Throwable
  final case class HttpRequestError(message: String = "Http request error") extends HttpError
}