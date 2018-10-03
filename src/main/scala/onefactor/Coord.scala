package onefactor

object Coord {

  def isRight(lon: Double, lat: Double): Boolean = {
    lon > -180 && lon < 180 && lat >= -90 && lat <= 90
  }

}
