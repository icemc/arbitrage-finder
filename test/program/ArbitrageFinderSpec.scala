package test.program

import test.UnitSpec
import src.services.*
import src.models.*
import src.program.ArbitrageFinder
import src.program.ArbitrageFinder.ResultPrinter.print
import src.models.graph.*
import org.scalatest.*
import org.scalatest.concurrent.*
import flatspec.*
import matchers.*

class ArbitrageFinderSpec extends UnitSpec {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  "ArbitrageFinder" should "be able to find arbitrage when sample data contains potential arbitrage" in {
    given dataSource: DataSource[String] = DummyDataSources.THREE

    whenReady(ArbitrageFinder.find[String]) {
      cycles =>
        cycles.size should be > 0
    }
  }

  "ArbitrageFinder" should "not find arbitrage when sample data doesn't contains potential arbitrage" in {
    given dataSource: DataSource[String] = DummyDataSources.FOUR

    whenReady(ArbitrageFinder.find[String]) {
      cycles =>
        cycles.size should be (0)
    }
  }

  "ArbitrageFinder printer" should "print NO ARBITRAGE OPPORTUNITIES FOUND when no arbitrage found" in {
      given dataSource: DataSource[String] = DummyDataSources.FOUR
      whenReady(ArbitrageFinder.find[String]) {
        cycles =>
        cycles.print should be ("NO ARBITRAGE OPPORTUNITIES FOUND")
      }
    }

  "ArbitrageFinder printer" should "print ARBITRAGE OPPORTUNITIES DETECTED when arbitrage found" in {
    given dataSource: DataSource[String] = DummyDataSources.ONE

    whenReady(ArbitrageFinder.find[String]) {
      cycles =>
        cycles.print should include ("ARBITRAGE OPPORTUNITIES DETECTED")
    }
  }
}
