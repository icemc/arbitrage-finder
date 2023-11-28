package src.models.graph

trait Graph[V, W](vertexAdjacencyMap: Map[Vertex[V], List[Edge[V,W]]], vertices: Set[Vertex[V]], edges: Set[Edge[V,W]]):
  def addEdge(startVertex: Vertex[V], weight: W, endVertex: Vertex[V]): Graph[V, W]
end Graph


final case class EmptyDirectedGraph[V, W] private[graph] () extends Graph[V, W](Map.empty, Set.empty, Set.empty):
  override def addEdge(startVertex: Vertex[V], weight: W, endVertex: Vertex[V]): DirectedGraph[V, W] =
    val edge = Edge(startVertex, endVertex, weight)
    new DirectedGraph(Map(startVertex -> List(edge)), Set(startVertex, endVertex), Set(edge))
  end addEdge
end EmptyDirectedGraph


final case class DirectedGraph[V, W] private[graph] (
                                                      vertexAdjacencyMap: Map[Vertex[V], List[Edge[V,W]]],
                                                      vertices: Set[Vertex[V]],
                                                      edges: Set[Edge[V,W]]
                                                    )
  extends Graph[V, W](
    vertexAdjacencyMap: Map[Vertex[V], List[Edge[V,W]]],
    vertices: Set[Vertex[V]],
    edges: Set[Edge[V,W]]
  ):
  override def addEdge(startVertex: Vertex[V], weight: W, endVertex: Vertex[V]): DirectedGraph[V, W] =
    val edge = Edge(startVertex, endVertex, weight)
    val updatedValue = edge :: vertexAdjacencyMap.getOrElse(startVertex, Nil)
    DirectedGraph(
      vertexAdjacencyMap = vertexAdjacencyMap.filterNot(_._1 == startVertex) ++ Map(startVertex -> updatedValue),
      vertices = vertices ++ Set(startVertex, endVertex),
      edges = edges + edge
    )
end DirectedGraph

object DirectedGraph {
  import src.models.graph.Vertex._
  def apply[V, W](tuples: (V, W, V)*): DirectedGraph[V, W] =
    tuples.foldLeft(DirectedGraph.empty[V, W])((graph, tuple) =>
            graph.addEdge(tuple._1, tuple._2, tuple._3)
    )

  private def empty[V, W]: DirectedGraph[V, W] = DirectedGraph[V, W](
    vertexAdjacencyMap = Map.empty[Vertex[V], List[Edge[V, W]]],
    vertices = Set.empty[Vertex[V]],
    edges = Set.empty[Edge[V, W]]
  )
}


object Graph {
  def empty[V, W]: Graph[V, W] = EmptyDirectedGraph[V, W]()
}
