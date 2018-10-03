package onefactor.grid

import akka.actor.{Actor, ActorRef, Props}
import onefactor.Coord
import onefactor.user.User

import scala.collection.mutable

object GridManager {

  case class UpdateCell(tileX: Int,
                        tileY: Int,
                        distanceError: Int)

  case class GetCell(lon: Double,
                     lat: Double,
                     requestor: ActorRef)

  case class CellAnswer(cell: Option[Cell],
                        requestor: ActorRef)

  def props(): Props = {
    Props(classOf[GridManager])
  }

}

class GridManager extends Actor {

  val cells = mutable.Map.empty[String, Cell]

  def key(c: Cell): String = {
    key(c.tileX, c.tileY)
  }

  def key(lon: Double, lat: Double): String = {
    s"${lon.toInt}:${lat.toInt}"
  }

  override def receive: Receive = {
    case GridManager.UpdateCell(tileX, tileY, distanceError) =>
      if (Coord.isRight(tileX, tileY)) {
        cells += (key(tileX, tileY) -> Cell(tileX, tileY, distanceError))
      }

    case GridManager.GetCell(lon, lat, requestor) =>
      sender() ! GridManager.CellAnswer(cells.get(key(lon, lat)), requestor)

    case user: User =>
      val lastSender = sender()
      cells.get(key(user.lon, user.lat)) match {
        case Some(cell: Cell) =>
          lastSender ! user.copy(isNextToLabel = Some(cell.isNextToLabel(user.lon, user.lat)))
        case None =>
          lastSender ! user.copy(isNextToLabel = None)
      }
  }

}
