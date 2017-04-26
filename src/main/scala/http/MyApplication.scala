package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol
import java.net.{URI, URLDecoder, URLEncoder}

import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Try


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val coordsResponseFormat = jsonFormat2(CoordsResponse)
  implicit val coordsFormat = jsonFormat4(Coords)
  implicit val errorFormat = jsonFormat1(ErrorJsonMessage)
  implicit val epsgFormat = jsonFormat1(EpsgCode)
  implicit val proj4Format = jsonFormat1(Proj4)
}

object MyApplication extends JsonSupport {

  val host = "localhost"
  val port = 8080

  implicit val system = ActorSystem("simple-rest-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  //Define the route
  val requestHandler = system.actorOf(RequestHandler.props(),"requestHandler")

  //Define the route
  val route : Route = {

    implicit val timeout = Timeout(20.seconds)

    path("getEpsgCode") {
      get {
        parameters('proj4.as[String]) { proj4 =>
          onSuccess(requestHandler ? GetEpsgCodeRequest(proj4)) {
            case response: GetEpsgCodeResponse =>
              complete(response.epsgCode)
            case response: String =>
              complete(response)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }~
    path("postEpsgCode") {
      post {
        entity(as[Proj4]) { proj4 =>
          onSuccess(requestHandler ? PostEpsgCodeRequest(proj4)) {
            case response: GetEpsgCodeResponse =>
              complete(response.epsgCode)
            case response:ErrorJsonMessage =>
              complete(response)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }~
    path("convertCoords") {
      post {
        entity(as[Coords]) { coords =>
          onSuccess(requestHandler ? ConvertCoordsRequest(coords)) {
            case response: CoordsResponse =>
              complete(response) // Complete the route and respond with the Health data
            case response:ErrorJsonMessage =>
              complete(response)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    //Startup, and listen for requests
    val bindingFuture = Http().bindAndHandle(route, host, port)
    println(s"Waiting for requests at http://$host:$port/...\nHit RETURN to terminate")
    StdIn.readLine()

    //Shutdown
    bindingFuture.flatMap(_.unbind())
    system.terminate()
  }
}