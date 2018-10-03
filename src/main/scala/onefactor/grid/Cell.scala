package onefactor.grid

object Cell {

  val radius = 6372795 // Earth radius

}

case class Cell(tileX: Int,
                tileY: Int,
                distanceError: Int) extends CellLike

trait CellLike {

  val tileX: Int

  val tileY: Int

  val distanceError: Int

  val lonR: Double = tileX * math.Pi / 180
  val latR: Double = tileY * math.Pi / 180

  val cosLatitude: Double = math.cos(latR)
  val sinLatitude: Double = math.sin(latR)

  def isNextToLabel(lon: Double, lat: Double): Boolean = {
    distance(lon, lat) <= distanceError
  }

  def distance(lon: Double, lat: Double): Double = {
    val latitude = lat * math.Pi / 180
    val sinLat = math.sin(latitude)
    val cosLat = math.cos(latitude)

    val delta = (lon * math.Pi / 180) - lonR
    val cosDelta = math.cos(delta)
    val sinDelta = math.sin(delta)

    val atan = math.atan2(
      math.sqrt(math.pow(cosLat * sinDelta, 2) + math.pow(cosLatitude * sinLat - sinLatitude * cosLat * cosDelta, 2)),
      sinLatitude * sinLat + cosLatitude * cosLat * cosDelta
    )
    Cell.radius * atan
  }

}
