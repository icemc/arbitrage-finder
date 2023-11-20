package test.http

import src.http.HttpUtil
import sttp.client3.*
import sttp.capabilities.*
import sttp.model.*
import test.UnitSpec
import org.scalatest.*
import org.scalatest.concurrent.*
import scala.concurrent.ExecutionContext

import scala.concurrent.Future
class HttpUtilSpec extends UnitSpec with HttpUtil {

  def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.global

  def backend: SttpBackend[Future, WebSockets] = HttpClientFutureBackend
    .stub
    .whenRequestMatches(_.method == Method.GET)
    .thenRespond(UnitSpec.json)

  "Http Utils" should "should be able to fetch data from API" in {
    whenReady(get("https://abanda.me")) { data =>
      data should include ("BTC")
      data should include ("CHSB")
      data should include ("EUR")
      data should include ("DAI")
    }
  }
}
