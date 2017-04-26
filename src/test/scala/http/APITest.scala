package http

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.util.ByteString
import akka.http.scaladsl.model._

class APITest extends WordSpec with Matchers with ScalatestRouteTest {

  "Proj4 API" should {
    "return epsg code on post request postEpsgCode" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "proj4":"+proj=longlat +datum=WGS84 +no_defs"
           |}
        """.stripMargin)

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/postEpsgCode",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      postRequest ~> MyApplication.route ~> check {
        responseAs[String] shouldEqual "4326"
      }
    }

    //Decoded +proj=longlat +datum=WGS84 +no_defs on https://meyerweb.com/eric/tools/dencoder/
    "return epsg code on get request getEpsgCode" in {
      Get("/getEpsgCode?proj4=%2Bproj%3Dlonglat%20%2Bdatum%3DWGS84%20%2Bno_defs") ~> MyApplication.route ~> check {
        responseAs[String] shouldEqual "4326"
      }
    }

    // Decoded wrong proj4
    "return error message on get request getEpsgCode" in {
      Get("/getEpsgCode?proj4=%2Bprdsoj%3Dlonglat%20%2Bdatum%3DWGS84%20%2Bno_defs") ~> MyApplication.route ~> check {
        responseAs[String] shouldEqual "The format is wrong!"
      }
    }
  }
}