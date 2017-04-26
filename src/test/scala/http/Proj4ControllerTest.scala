import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter

import scala.collection.mutable.Stack
import geotrellis.proj4._
import http.Proj4Controller._
import org.scalactic.TolerantNumerics

class Proj4ControllerTest extends FunSpec with BeforeAndAfter {

	object MathUtils {
		def ~=(x: Double, y: Double, precision: Double) = {
			if ((x - y).abs < precision) true else false
		}
	}

	describe("Geotrellis proj4") {
		it("should return the proj4string corresponding to EPSG:4326") {
		    val crs = CRS.fromName("EPSG:4326")

		    val proj4string = crs.toProj4String
		    val string = "+proj=longlat +datum=WGS84 +no_defs "

		    assert(proj4string == string)
	  	}

		it("should return epsg value and error the proj4 string (EPSG:4326)") {
				val proj4string = "+proj=longlat +datum=WGS84 +no_defs "
				val proj4stringWithError = "+proj=longlattt +datum=WGS84 +no_defs "

				assert(getEpsg(proj4string) == "4326")
				assert(getEpsg(proj4stringWithError) == "The format is wrong!")
		}

		it("should convert right (controlled with https://mygeodata.cloud/cs2cs/)") {
				val proj4From = "+proj=longlat +datum=WGS84 +no_defs"
				val proj4To = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs"
				val x = 113.4
				val y = 46.78

				val trueCoords = (12623630.256,5906238.1135)
				val coords = convertCoords(x, y, proj4From, proj4To)
				val reverted_coords = convertCoords(coords.get._1, coords.get._2, proj4To, proj4From)

				assert(MathUtils.~=(trueCoords._1, coords.get._1, 0.01))
				assert(MathUtils.~=(trueCoords._2, coords.get._2, 0.01))
				assert(MathUtils.~=(x, reverted_coords.get._1, 0.01))
				assert(MathUtils.~=(y, reverted_coords.get._2, 0.01))
		}
	}
}
