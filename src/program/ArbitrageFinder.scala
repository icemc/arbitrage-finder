package src.program

import src.models.BellmanFord
import src.models.graph.{DirectedGraph, Vertex}
import src.services.DataSource

import java.text.DecimalFormat
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

object ArbitrageFinder{

  /**
   * Given a data source from which exchange rates can be obtained and an execution context find Arbitrage opportunities
   * @param datasource the exchange rate data source
   * @param executionContext the execution context
   * @tparam T Vertex type param
   * @return arbitrage opportunities
   */
  def find[T](using datasource: DataSource[T], executionContext: ExecutionContext): Future[Map[List[Vertex[T]], Double]] = {

    @tailrec
    def calculateArbitrage(index: Int, totalArbitrage: Double, currentCycles: List[Vertex[T]], graph: DirectedGraph[T, Double]): Double = {
      if (index == currentCycles.size) totalArbitrage
      else {
        val from = currentCycles(index - 1)
        val to = currentCycles(index)

        val newArbitrage = graph.vertexAdjacencyMap(from)
          .find(edge => edge.startVertex == from && edge.endVertex == to).map(edge => edge.weight + totalArbitrage)
          .getOrElse(totalArbitrage)

        calculateArbitrage(index + 1, newArbitrage, currentCycles, graph)
      }
    }

    for {
      data <- datasource.getExchangeData.map(_.map(edge => (edge._1, -1 * Math.log(edge._2), edge._3)))
      graph = DirectedGraph[T, Double](data:_*)
      cycles <- Future.sequence(graph.vertices.toList.map(vertex => Future.successful(BellmanFord(graph).findCyclesAtVertex(vertex))))
      cycles <- Future.traverse(graph.vertices.toList)(vertex => Future.successful(BellmanFord(graph).findCyclesAtVertex(vertex)))
//      cycles = BellmanFord(graph).findCycles
    } yield
      cycles.flatten.foldLeft(Map.empty[List[Vertex[T]], Double])((values, cycle) =>
      values.updated(cycle, approximateArbitrageValue(calculateArbitrage(1, 0, cycle, graph)))
    )
  }

  private def approximateArbitrageValue(totalArbitrage: Double): Double =
    Math.pow(2.0, totalArbitrage * -1) - 1
  end approximateArbitrageValue


  object ResultPrinter:
    private val NO_OPPORTUNITIES: String = "NO ARBITRAGE OPPORTUNITIES FOUND"
    private val HEADER: String = "ARBITRAGE OPPORTUNITIES DETECTED"
    private val HEADER_SEPARATOR: String = "======================================================"
    private val PATH_SEPARATOR: String = ","
    private val PATH_START: String = "Path: ["
    private val PATH_END: String = "]"

    /**
     * Pretty prints the found arbitrage path and values
     */
    extension [T](result: Map[List[Vertex[T]], Double])
      def print: String =
        if result.isEmpty then  NO_OPPORTUNITIES
        else
          val start = s"\n$HEADER\n$HEADER_SEPARATOR\n"
          result.foldLeft(start)((string, cycle) =>
            s"$string$PATH_START${cycle._1.mkString(PATH_SEPARATOR)}$PATH_END -> ${cycle._2}\n"
          )
    end extension
  end ResultPrinter

}
