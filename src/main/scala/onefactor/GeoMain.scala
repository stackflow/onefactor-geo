package onefactor

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import onefactor.grid.{CellProducer, GridManager}
import onefactor.user.{UserManager, UserProducer}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object GeoMain extends App with Api with LazyLogging {
  logger.info("oneFactor Geo Service started")

  implicit val system: ActorSystem = ActorSystem("onefactor-geo")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContext = system.dispatcher

  val gridManager = system.actorOf(GridManager.props())
  val userManager = system.actorOf(UserManager.props(gridManager))

  this.args match  {
    case Array(userPath, gridPath, _*) =>
      UserProducer.load(userPath)(userManager)
      CellProducer.load(gridPath)(gridManager)
    case Array(userPath, _*) =>
      UserProducer.load(userPath)(userManager)
    case _ =>
  }

  val interface: String = "localhost"
  val port: Int = 8080

  lazy val routes: Route = apiRoutes()

  Http().bindAndHandle(routes, interface, port)

  logger.info(s"Server online at http://$interface:$port/")

  Await.result(system.whenTerminated, Duration.Inf)

}
