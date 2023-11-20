package test.models

import test.UnitSpec
import src.services.*
import src.models.*
import src.models.graph.*
import org.scalatest.*
import org.scalatest.concurrent.*
import flatspec.*
import matchers.*

class BellmanFordSpec extends UnitSpec {
  "Bellman Ford algorithm" should "not find negative cycles in non negative cycle graphs" in {
    whenReady(DummyDataSources.FOUR.getExchangeData){ data =>
      BellmanFord(DirectedGraph[String, Double](data:_*)).findCycles.size  should be (0)
    }
  }

  "Bellman Ford algorithm" should "find negative cycles in graphs with negative cycle" in {
    whenReady(DummyDataSources.TWO.getExchangeData) { data =>
      BellmanFord(DirectedGraph[String, Double](data: _*)).findCycles should contain
        List(Vertex("2"), Vertex("4"), Vertex("3"), Vertex("2"))
    }
  }

  "Bellman Ford algorithm" should "find negative cycles in graphs with negative cycle 2" in {
    whenReady(DummyDataSources.ONE.getExchangeData) { data =>
      BellmanFord(DirectedGraph[String, Double](data: _*)).findCycles should contain
      List(Vertex("USD"), Vertex("EUR"), Vertex("CAD"), Vertex("USD"))
    }
  }

  "Bellman Ford algorithm" should "find negative cycles in graphs with negative cycle 3" in {
    whenReady(DummyDataSources.THREE.getExchangeData) { data =>
      BellmanFord(DirectedGraph[String, Double](data: _*)).findCycles should contain
      List(Vertex("DAI"), Vertex("CHSB"), Vertex("DAI"))
    }
  }
}
