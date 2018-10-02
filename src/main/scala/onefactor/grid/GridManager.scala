package onefactor.grid

import akka.actor.{Actor, Props}
import onefactor.user.User

import scala.collection.mutable

object GridManager {

  case class UpdateCell(cell: Cell)

  def props(): Props = {
    Props(classOf[GridManager])
  }

}

class GridManager extends Actor {

  val cells = mutable.Map.empty[String, Cell]

  override def receive: Receive = {
    case GridManager.UpdateCell(c) =>
      cells += (s"${c.tileX}_${c.tileY}" -> c)

    case user: User =>
      val lastSender = sender()
      cells.get(s"${user.lon.toInt}_${user.lat.toInt}") match {
        case Some(cell: Cell) =>
          lastSender ! user.copy(isNextToLabel = Some(cell.isNextToLabel(user.lon, user.lat)))
        case None =>
          lastSender ! user.copy(isNextToLabel = None)
      }
  }

}
