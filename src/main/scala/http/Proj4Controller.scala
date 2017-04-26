package http

import geotrellis.proj4.CRS
import org.osgeo.proj4j.{CRSFactory, CoordinateTransformFactory, ProjCoordinate}
import org.osgeo.proj4j.units.Angle

object Proj4Controller {

  /**
  Returns String EPSG code from proj4 String
   */
  def getEpsg(proj4:String):String = {
    convertEpsg(proj4) match {
      case Some(i) => i.toString()
      case None => "The format is wrong!"
    }
  }

  /**
  Getting EPSG code from proj4 String
   */
  def convertEpsg(proj4:String):Option[Int] = {
    try {
      CRS.fromString(proj4).epsgCode
    } catch {
      case e:Exception => None
    }
  }

  /**
  Converting coordinates from one projection to another
    */
  def convertCoords(x:Double, y: Double, proj4from:String, proj4to:String):Option[(Double,Double)] = {
    try {
      val epsgFrom = convertEpsg(proj4from)
      val epsgTo = convertEpsg(proj4to)

      val crsFactory = new CRSFactory()
      var src = crsFactory.createFromParameters(epsgFrom.toString(), proj4from)
      val dest = crsFactory.createFromParameters(epsgTo.toString(), proj4to)

      val ctf = new CoordinateTransformFactory()
      val transform = ctf.createTransform(src, dest)

      val srcPt = new ProjCoordinate(x, y)
      val destPt = new ProjCoordinate()

      transform.transform(srcPt, destPt)
      Some(destPt.x, destPt.y)
    } catch {
      case e:Exception => None
    }
  }
}
