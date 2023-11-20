package src.models.graph

final case class Edge[+V, +W](startVertex: Vertex[V], endVertex: Vertex[V], weight: W)
