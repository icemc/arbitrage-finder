#!/usr/bin/env -S scala-cli shebang

/* Borger, feel free to let your imagination shine but do not change this snippet. */
val url: String = args.length match {
  case 0 => "https://api.swissborg.io/v1/challenge/rates"
  case _ => args(0)
}

/* Add your stuff, be Awesome! */

import sttp.client3.*
import sttp.capabilities.*
import sttp.model.*
import src.models.graph.Vertex.*
import src.program.ArbitrageFinder
import src.program.ArbitrageFinder.ResultPrinter.print
import src.services.{SwissBorgDataSource, *}

import java.time.Instant
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


//Prepare dependencies
given globalExecutionContext: ExecutionContext = ExecutionContext.global
given backend: SttpBackend[Future, WebSockets] = HttpClientFutureBackend()
given dataSource: DataSource[String] = SwissBorgDataSource()

val start = System.currentTimeMillis
val result = ArbitrageFinder.find.map{result =>
  println(s"${result.print}\nTime: ${System.currentTimeMillis - start}ms")
}

Await.ready(result, Duration.Inf)