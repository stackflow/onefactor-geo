package onefactor.user

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._

import scala.util.Success

object UserApi {

  case class Request(lon: Double, lat: Double)

}

trait UserApi {

  implicit val timeout: Timeout

  val userManager: ActorRef

  implicit val printer: Printer

  val userRoute: Route = {
    pathPrefix("users") {
      pathEndOrSingleSlash {
        post {
          entity(as[UserApi.Request]) { req =>
            val userId = UUID.randomUUID().toString
            onComplete(userManager ? UserManager.UpdateUser(userId, req.lon, req.lat)) {
              case Success(user: User) =>
                complete(user.asJson)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ get {
          parameters('lon.as[Double], 'lat.as[Double]) { (lon, lat) =>
            onComplete(userManager ? UserManager.GetUsers(lon, lat)) {
              case Success(Some(users: Users)) =>
                complete(users.asJson)
              case Success(None) =>
                complete(StatusCodes.NotFound, "Cell is not found")
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        }
      } ~ path(JavaUUID) { userUUID =>
        val userId = userUUID.toString
        get {
          onComplete(userManager ? UserManager.GetUser(userId)) {
            case Success(user: User) =>
              complete(user.asJson)
            case Success(ex: UserManager.UserNotFoundException) =>
              complete(StatusCodes.NotFound, ex.getMessage)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ put {
          entity(as[UserApi.Request]) { req =>
            onComplete(userManager ? UserManager.UpdateUser(userId, req.lon, req.lat)) {
              case Success(user: User) =>
                complete(user.asJson)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ delete {
          onComplete(userManager ? UserManager.DeleteUser(userId)) {
            case Success(_) =>
              complete(s"User is deleted: $userId")
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

}
