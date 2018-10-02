package onefactor.user

import java.util.UUID

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.util.{Failure, Success}

object UserApi {

  case class Request(lon: Double, lat: Double)

}

trait UserApi extends LazyLogging {

  implicit val timeout: Timeout

  val userManager: ActorRef

  val userRoute: Route = {
    pathPrefix("user") {
      pathEndOrSingleSlash {
        post {
          entity(as[UserApi.Request]) { req =>
            onComplete(userManager ? UserManager.UpdateUser(UUID.randomUUID().toString, req.lon, req.lat)) {
              case Success(user: User) =>
                complete(user)
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
              complete(user)
            case Success(ex: UserManager.UserNotFoundException) =>
              complete(StatusCodes.NotFound, ex.getMessage)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        } ~ put {
          entity(as[UserApi.Request]) { req =>
            onComplete(userManager ? UserManager.UpdateUser(userId, req.lon, req.lat)) {
              case Success(user: User) =>
                complete(user)
              case _ =>
                complete(StatusCodes.InternalServerError)
            }
          }
        } ~ delete {
          onComplete(userManager ? UserManager.DeleteUser(userId)) {
            case Success(_) =>
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

}
