package test.services

import src.services.*
import sttp.client3.*
import sttp.capabilities.*
import sttp.model.*
import test.UnitSpec
import org.scalatest.*
import org.scalatest.concurrent.*

import scala.concurrent.Future

class SwissBorgDataSourceSpec extends UnitSpec {

  given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  given backend: SttpBackend[Future, WebSockets] = HttpClientFutureBackend
    .stub
    .whenRequestMatches(_.method == Method.GET)
    .thenRespond(UnitSpec.json)

  "SwissBorg Data Source class" should "should be able to fetch exchange data from API" in {
    whenReady(SwissBorgDataSource().getExchangeData) { data =>
        data.size should be > (0)
    }
  }

  "SwissBorg Data Source class" should "should be able to parse data obtained from API" in {
    whenReady(SwissBorgDataSource().getExchangeData) { data =>
      data should contain  ("DAI", 1.06842243, "EUR")
      data should contain  ("EUR", 33840.89308257, "BTC")
      data should contain  ("CHSB", 5.21352061, "EUR")
    }
  }

}
