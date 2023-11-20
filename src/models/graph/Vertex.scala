package src.models.graph

final case class Vertex[+T](id: T):
  override def toString: String = id.toString
end Vertex


object Vertex:
  given toVertex[T]: Conversion[T, Vertex[T]] with
    def apply(s: T): Vertex[T] = Vertex(s)
end Vertex

