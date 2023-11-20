package src.services

import scala.concurrent.Future

trait DataSource[T] :
  def getExchangeData: Future[Seq[(T, Double, T)]]
end DataSource

object DummyDataSources:

  object ONE extends DataSource[String]:
   override def getExchangeData:Future[Seq[(String, Double, String)]] =  Future.successful(Seq(
     ("USD", 0.741, "EUR"),
     ("USD", 0.657, "GBP"),
     ("USD", 1.061, "CHF"),
     ("USD", 1.005, "CAD"),
     ("EUR", 1.349, "USD"),
     ("EUR", 0.888, "GBP"),
     ("EUR", 1.433, "CHF"),
     ("EUR", 1.366, "CAD"),
     ("GBP", 1.521, "USD"),
     ("GBP", 1.126, "EUR"),
     ("GBP", 1.614, "CHF"),
     ("GBP", 1.538, "CAD"),
     ("CHF", 0.942, "USD"),
     ("CHF", 0.698, "EUR"),
     ("CHF", 0.619, "GBP"),
     ("CHF", 0.953, "CAD"),
     ("CAD", 0.995, "USD"),
     ("CAD", 0.732, "EUR"),
     ("CAD", 0.650, "GBP"),
     ("CAD", 1.049, "CHF")
   ))
  end ONE
  
  object TWO extends DataSource[String]:
    //Has a negative weigh cycle but will not work with Arbitrage finder due to the values of its weight
    //However BellmanFord.findCycles should be able to find the negative weight cycle
    override def getExchangeData: Future[Seq[(String, Double, String)]] = Future.successful(Seq(
          ("2", 5, "4"),
          ("3", -10, "2"),
          ("4", 3, "3"),
          ("1", 5, "4"),
          ("1", 4, "2")
    ))
  end TWO

  object THREE extends DataSource[String]:
    override def getExchangeData: Future[Seq[(String, Double, String)]] = Future.successful(Seq(
          ("BTC", 116352.2654440156, "CHSB"),
          ("BTC", 23524.1391553039, "DAI"),
          ("BTC", 23258.8865583847, "EUR"),
          ("CHSB", 0.0000086866, "BTC"),
          ("CHSB", 0.2053990550, "DAI"),
          ("CHSB", 0.2017539914, "EUR"),
          ("DAI", 0.0000429088, "BTC"),
          ("DAI", 4.9320433378, "CHSB"),
          ("DAI", 0.9907652193, "EUR"),
          ("EUR", 0.0000435564, "BTC"),
          ("EUR", 5.0427577751, "CHSB"),
          ("EUR", 1.0211378960, "DAI")
    ))
  end THREE

  object FOUR extends DataSource[String]:
    override def getExchangeData: Future[Seq[(String, Double, String)]] = Future.successful(Seq(
      ("3", -10, "2"),
      ("4", 3, "3"),
      ("1", 5, "4"),
      ("1", 4, "2")
    ))
  end FOUR
end DummyDataSources
