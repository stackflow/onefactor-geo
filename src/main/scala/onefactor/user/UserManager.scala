package onefactor.user

import akka.actor.{Actor, ActorRef, Props}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object UserManager {

  sealed trait Request

  case class UpdateUser(id: String,
                        lon: Double,
                        lat: Double) extends Request

  case class DeleteUser(id: String) extends Request

  case class GetUser(id: String) extends Request

  def props(gridManager: ActorRef): Props = {
    Props(classOf[UserManager], gridManager)
  }

  class UserNotFoundException(message: String) extends Exception(message)

}

class UserManager(gridManager: ActorRef) extends Actor with LazyLogging {

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
      if (lon < -180 || lon > 180 || lat < -90 || lat > 90) {
        sender() ! new IllegalArgumentException("Wrong coordinates!")
      } else {
        val user = User(id, lon, lat, None)
        users += (id -> user)
        gridManager forward user
      }

    case UserManager.DeleteUser(id: String) =>
      val user = users.remove(id)
      sender() ! user
  }

}