package src.models

import src.models.BellmanFord.*
import src.models.graph.{DirectedGraph, Edge, Vertex}

import scala.annotation.tailrec

/**
 * An implementation of Bellman ford algorithm to find negative weight cycles in graph
 * @param graph input graph
 * @tparam V Vertex type
 */
final case class BellmanFord[V](graph: DirectedGraph[V, Double]):

  /**
   * Find negative weight cycles in graph using Bellman Ford algorithm.
   * For each vertex in graph find negative weight cycles.
   *
   * Time complexity: O(V.V.E) or O(V&#94;4) (in case of a complete graph) 
   * where V = number of vertices, E = number of edges in graph
   *
   * @return
   */
  def findCycles: Cycles[V] =
    getUniqueCycles(
      graph.vertices
        .foldLeft(
            List.empty[List[Vertex[V]]]
        )((cycles, vertex) => cycles ++ findCyclesAtVertex(vertex))
    )
  end findCycles

  /**
   * Find cycles at source vertex
   * Time complexity: O(V.E)
   *
   * @param source
   * @return a tuple containing list of cycles, distances table and Predecessors table
   */
   def findCyclesAtVertex(
                          source: Vertex[V],
                        ): Cycles[V] =
    if graph.vertices.contains(source) then
      val distanceAndPredecessors = graph.vertices.foldLeft(Map.empty[Vertex[V], Double], Map.empty[Vertex[V], Vertex[V]])((tuple, vertex) =>
        (tuple._1.updated(vertex, Double.PositiveInfinity), tuple._2.updated(vertex, vertex))
      )

      val relaxed = relaxEdges(distanceAndPredecessors._1.updated(source, 0.0), distanceAndPredecessors._2)
      findNegativeWeightCycles(relaxed._1, relaxed._2)._1.distinct
    else Nil

  end findCyclesAtVertex


  /**
   * Relax All edges in graph using distance and predecessors table.
   * Time complexity: O(V.E)
   *
   * @param distances distance table
   * @param predecessors predecessors table
   * @return new distances and predecessors tables
   */
  private def relaxEdges(
                          distances: Distances[V],
                          predecessors: Predecessors[V]
                        ): (Distances[V], Predecessors[V]) =

    /**
     * Calculate new distance and predecessors using edge E(index)
     * Time complexity: O(V)
     *
     * @param index
     * @param distances
     * @param predecessors
     * @return
     */
    @tailrec
    def relax(
               index: Int,
               distances: Distances[V],
               predecessors: Predecessors[V]
             ): (Distances[V], Predecessors[V]) =
      if index < distances.size then
        val distancesAndPredecessors = graph.edges.foldLeft(distances, predecessors) { (disAndPreds, edge) =>
          if disAndPreds._1(edge.endVertex) > disAndPreds._1(edge.startVertex) + edge.weight then
            (disAndPreds._1.updated(edge.endVertex, disAndPreds._1(edge.startVertex) + edge.weight),
              disAndPreds._2.updated(edge.endVertex, edge.startVertex))
          else disAndPreds
        }
        relax(index + 1, distancesAndPredecessors._1, distancesAndPredecessors._2)
      else
        (distances, predecessors)

    end relax

    relax(1, distances, predecessors)
  end relaxEdges



  /**
   * Find negative weight cycles using given distances and predecessors from source vertex
   *Time complexity: O(E.V)
   *
   * @param distances distances from source vertex after relaxation
   * @param predecessors predecessors map obtained after relaxation
   * @return a tuple containing list of cycles, distances table and Predecessors table
   */
  private def findNegativeWeightCycles(
                                        distances: Distances[V],
                                        predecessors: Predecessors[V]
                                      ): (Cycles[V], Distances[V], Predecessors[V]) =

    /**
     * Finds cycle by moving backward from vertex A to vertex A
     * Time complexity: O(E + V)
     *
     * @param seenVertices already visited vertices
     * @param cycle current path
     * @param currentVertex current visiting vertex
     * @param currentEdge current visiting edge
     * @return a tuple containing new found cycle path, seen vertices and current vertex
     */
    @tailrec
    def findCycle(
                            seenVertices: Set[Vertex[V]],
                            cycle: List[Vertex[V]],
                            currentVertex: Vertex[V],
                            currentEdge: Edge[V, Double]
                          ): (List[Vertex[V]], Set[Vertex[V]], Vertex[V]) =
      if currentVertex != currentEdge.endVertex && !cycle.contains(currentVertex) then
        findCycle(
          seenVertices + currentVertex,
          cycle :+ currentVertex,
          predecessors(currentVertex),
          currentEdge
        )
      else (cycle, seenVertices, currentVertex)
    end findCycle


    val cycles = graph.edges.foldLeft(List.empty[List[Vertex[V]]]){
      (cycles, edge) =>
        if distances(edge.endVertex) > (distances(edge.startVertex) + edge.weight) then
          //Negative cycles detected. Process them if possible
          val currentVertex = edge.endVertex
          val (cycle, _, vertex) = findCycle(Set(currentVertex), List(currentVertex), predecessors(currentVertex), edge)
          val newCycle = cycle.appended(vertex)

          //Use DummyDataSources.ONE to see how index is useful in cutting off precycle nodes
//          println(s"""cycle: $newCycle""")
          val index = newCycle.indexOf(vertex)

          cycles :+ newCycle.drop(index).reverse
        else cycles //No negative cycles return original values
    }

    (cycles, distances, predecessors)
  end findNegativeWeightCycles


  private def getUniqueCycles(cycles: Cycles[V]): Cycles[V] =
    cycles.distinct

end BellmanFord

object BellmanFord:
  type Distances[V]    = Map[Vertex[V], Double]
  type Cycles[V]       = List[List[Vertex[V]]]
  type Predecessors[V] = Map[Vertex[V], Vertex[V]]
end BellmanFord

