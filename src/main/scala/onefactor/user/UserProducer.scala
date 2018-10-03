package onefactor.user

import java.nio.file.{Files, Paths}
import java.util.UUID

import akka.actor.ActorRef
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object UserProducer extends LazyLogging {

  def load(pathname: String)(implicit userManager: ActorRef) = Future {
    logger.info(s"Getting users from file started: $pathname")
    val path = Paths.get(pathname)
    Files.lines(path).forEach { line =>
      val params = line.split("\t")
      if (params.length > 2) {
        for {
          id <- Try(UUID.fromString(params(0)))
          lon <- Try(params(1).toDouble)
          lat <- Try(params(2).toDouble)
        } yield {
          userManager ! UserManager.AddUser(id.toString, lon, lat)
        }
      }
    }
    logger.info(s"Getting users from file finished")
  }

}
