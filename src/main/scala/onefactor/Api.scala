package onefactor

import akka.http.scaladsl.server.Route
import akka.util.Timeout
import onefactor.user.UserApi

import scala.concurrent.duration._

trait Api extends UserApi {

  implicit val timeout: Timeout = 5.second

  def apiRoutes(): Route = userRoute

}
