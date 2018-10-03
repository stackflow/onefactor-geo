package onefactor.user

import akka.actor.{Actor, ActorRef, Props}
import onefactor.Coord
import onefactor.grid.GridManager

import scala.collection.mutable

object UserManager {

  sealed trait Request

  case class AddUser(id: String,
                     lon: Double,
                     lat: Double) extends Request // for UserProducer

  case class UpdateUser(id: String,
                        lon: Double,
                        lat: Double) extends Request

  case class DeleteUser(id: String) extends Request

  case class GetUser(id: String) extends Request

  case class GetUsers(lon: Double,
                      lat: Double) extends Request

  def props(gridManager: ActorRef): Props = {
    Props(classOf[UserManager], gridManager)
  }

  class UserNotFoundException(message: String) extends Exception(message)

}

class UserManager(gridManager: ActorRef) extends Actor {

  val users = mutable.Map.empty[String, User]

  override def receive: Receive = {
    case UserManager.GetUser(id: String) =>
      users.get(id) match {
        case Some(user) =>
          gridManager forward user
        case _ =>
          sender() ! new UserManager.UserNotFoundException(s"User not found (userId: $id)")
      }

    case UserManager.UpdateUser(id: String, lon: Double, lat: Double) =>
      if (Coord.isRight(lon, lat)) {
        val user = User(id, lon, lat, None)
        users += (id -> user)
        gridManager forward user
      } else {
        sender() ! new IllegalArgumentException("Wrong coordinates!")
      }

    case UserManager.DeleteUser(id: String) =>
      val user = users.remove(id)
      sender() ! user

    case UserManager.AddUser(id: String, lon: Double, lat: Double) => // for UserProducer
      if (Coord.isRight(lon, lat)) {
        val user = User(id, lon, lat, None)
        users += (id -> user)
      }

    case UserManager.GetUsers(lon, lat) =>
      val lastSender = sender()
      gridManager ! GridManager.GetCell(lon, lat, lastSender)

    case GridManager.CellAnswer(cellOpt, requestor) => // message from GridManager, answer to GetCell
      cellOpt match {
        case Some(cell) =>
          val count = users.count { case (_, user) =>
            user.lon.toInt == cell.tileX && user.lat.toInt == cell.tileY
          }
          requestor ! Some(Users(cell.tileX, cell.tileY, count))
        case None =>
          requestor ! None
      }
  }

}