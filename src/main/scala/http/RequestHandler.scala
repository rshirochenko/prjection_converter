package http

import akka.actor.{Actor, ActorLogging, Props}

object RequestHandler {
  def props(): Props = {
    Props(classOf[RequestHandler])
  }
}

/* Case classes that describes messages */
case class Proj4(proj4:String)
case class EpsgCode(epsgCode: String)
case class Coords(x: Double, y: Double, projFrom: String, projTo: String)
case class CoordsResponse(x: Double, y: Double)
case class ErrorJsonMessage(error:String)

/* Case classes that describes response and request for getting EPSG code methods*/
case class PostEpsgCodeRequest(proj4: Proj4)
case class GetEpsgCodeRequest(proj4: String)
case class GetEpsgCodeResponse(epsgCode: String)

/* Case classes that describes response and request for converting coordinates methods*/
case class ConvertCoordsRequest(coords: Coords)
case class ConvertCoordsResponse(coordsResponse: CoordsResponse)

class RequestHandler extends Actor with ActorLogging{

  var coords: Coords = Coords(0.0,0.0,"","")

  def receive: Receive = {

    case GetEpsgCodeRequest(proj4) =>
      log.debug("Received GetEpsgCodeRequest")
      val epsgCode = Proj4Controller.convertEpsg(proj4)
      val result = epsgCode match {
        case Some(i) => {
          GetEpsgCodeResponse(i.toString)
        }
        case None => "The format is wrong!"
      }
      sender() ! result

    case PostEpsgCodeRequest(proj4) =>
      log.debug("Received PostEpsgCodeRequest")
      val epsgCode = Proj4Controller.convertEpsg(proj4.proj4)
      val result = epsgCode match {
        case Some(i) => {
          GetEpsgCodeResponse(i.toString)
        }
        case None => ErrorJsonMessage("The format is wrong!")
      }
      sender() ! result

    case request: ConvertCoordsRequest =>
      log.debug("Converting coords")
      coords = request.coords
      val coordsConverted = Proj4Controller.convertCoords(
        coords.x, coords.y, coords.projFrom, coords.projTo
      )
      val result = coordsConverted match  {
        case Some(i) => {
          CoordsResponse(i._1, i._2)
        }
        case None => ErrorJsonMessage("The format is wrong!")
      }
      sender() ! result
  }
}