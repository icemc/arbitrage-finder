package src.services

import sttp.client3.*
import sttp.capabilities.*
import sttp.model.*
import io.circe
import io.circe.*
import io.circe.syntax.*
import src.http.HttpUtil

import scala.concurrent.{ExecutionContext, Future}

final case class SwissBorgDataSource()(using val backend: SttpBackend[Future, WebSockets], val executionContext: ExecutionContext)
  extends DataSource[String] with HttpUtil :
  private val API_URL: String = "https://api.swissborg.io/v1/challenge/rates"

  given headers: Seq[Header] = Seq(Header(HeaderNames.Accept, "application/json"))

  override def getExchangeData: Future[Seq[(String, Double, String)]] = get(API_URL).map(extractData)

  private def extractData(json: String): Seq[(String, Double, String)] = 
    val rates = circe.parser.parse(json).getOrElse(Json.Null).hcursor.downField("rates")
    val keys = rates.keys.getOrElse(Nil)
    keys.foldLeft(List.empty[(String, Double, String)]) {
      (values, key) =>
        rates.downField(key).as[String] match {
          case Left(_) => values
          case Right(value) =>
            val currencies = key.split("-").take(2)
            if currencies.length < 2 || value.toDoubleOption.isEmpty || currencies(0) == currencies(1) then
              values
            else values :+ (currencies.head, value.toDouble, currencies.tail.head)
        }
    }
  end extractData
end SwissBorgDataSource
