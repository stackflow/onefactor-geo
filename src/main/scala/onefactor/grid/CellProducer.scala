package onefactor.grid

import java.nio.file.{Files, Paths}
import java.util.UUID

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging
import onefactor.user.UserManager

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

object CellProducer extends LazyLogging {

  def load(pathname: String)(implicit gridManager: ActorRef) = Future {
    logger.info(s"Getting cells from file started: $pathname")
    val path = Paths.get(pathname)
    Files.lines(path).forEach { line =>
      val params = line.split("\t")
      if (params.length == 3) {
        for {
          tileX <- Try(params(0).toInt)
          tileY <- Try(params(1).toInt)
          distanceError <- Try(params(2).toInt)
        } yield {
          gridManager ! GridManager.UpdateCell(Cell(tileX, tileY, distanceError))
        }
      }
    }
    logger.info(s"Getting cells from file finished")
  }

}
