package onefactor.grid

import java.nio.file.{Files, Paths}

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object CellProducer extends LazyLogging {

  def load(pathname: String)(implicit gridManager: ActorRef) = Future {
    logger.info(s"Getting cells from file started: $pathname")
    val path = Paths.get(pathname)
    Files.lines(path).iterator().asScala foreach { line =>
      val params = line.split("\t")
      if (params.length > 2) {
        for {
          tileX <- Try(params(0).toInt)
          tileY <- Try(params(1).toInt)
          distanceError <- Try(params(2).toInt)
        } yield {
          gridManager ! GridManager.UpdateCell(tileX, tileY, distanceError)
        }
      }
    }
    logger.info(s"Getting cells from file finished")
  }

}
