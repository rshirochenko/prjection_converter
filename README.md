Support API commands:

1. To convert coordinates from one projection to another (POST request)

POST http://localhost:8080/convertCoords

Examle JSON data:
{ 
	"x": 1312.4,
  	"y": 16.78,
  	"projFrom":"+proj=longlat +datum=WGS84 +no_defs",
    "projTo":"+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +wktext  +no_defs"
}

2. To get EPSG code number from proj4 string:

2.1 By GET request. The parameter should be encoded. 

Example:
For proj4 value "+proj=longlat +datum=WGS84 +no_defs"

http://localhost:8080/getEpsgCode?proj4=%2Bproj%3Dlonglat%20%2Bdatum%3DWGS84%20%2Bno_defs

2.2 By POST request.
http://localhost:8080/postEpsgCode

Example JSON data:
{
	"proj4":"+proj=longlat +datum=WGS84 +no_defs"
}
