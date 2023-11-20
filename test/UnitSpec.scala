package test

import org.scalatest.*
import org.scalatest.concurrent.*
import flatspec.*
import matchers.*
abstract class UnitSpec extends AnyFlatSpec with should.Matchers with ScalaFutures with IntegrationPatience

object UnitSpec:
  val json = """{"rates":{"BTC-BTC":"1.00000000","BTC-CHSB":"0.00000568","BTC-DAI":"0.00002772","BTC-EUR":"0.00002936","CHSB-BTC":"170820.09751120","CHSB-CHSB":"1.00000000","CHSB-DAI":"4.69380966","CHSB-EUR":"5.21352061","DAI-BTC":"35846.74465483","DAI-CHSB":"0.21166344","DAI-DAI":"1.00000000","DAI-EUR":"1.06842243","EUR-BTC":"33840.89308257","EUR-CHSB":"0.19056373","EUR-DAI":"0.90809120","EUR-EUR":"1.00000000"}}"""
end UnitSpec

