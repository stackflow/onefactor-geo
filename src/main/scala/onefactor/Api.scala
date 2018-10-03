package onefactor

import akka.http.scaladsl.server.Route
import akka.util.Timeout
import io.circe.Printer
import onefactor.user.UserApi

import scala.concurrent.duration._

trait Api extends UserApi {

  implicit val timeout: Timeout = 5.second

  implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def apiRoutes(): Route = userRoute

}
