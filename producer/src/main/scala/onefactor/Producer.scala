package onefactor

import java.nio.file._
import java.util.UUID

object Producer extends App {

  val r = new scala.util.Random

  val maxNumberOfUsers = 1000000
  val minNumberOfUsers = 100000
  val numberOfUsers = minNumberOfUsers + r.nextInt(maxNumberOfUsers - minNumberOfUsers)
  val usersFilename = "geo-users.tsv"
  println(s"User generation started ($numberOfUsers)")
  val usersPath: Path = Paths.get(usersFilename)
  Files.write(usersPath, Array.emptyByteArray, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
  (0 to numberOfUsers) foreach { _ =>
    val longitude = r.nextFloat * 360 - 180
    val latitude = r.nextFloat * 180 - 90
    val str = s"${UUID.randomUUID().toString}\t$longitude\t$latitude\n"
    Files.write(usersPath, str.getBytes, StandardOpenOption.APPEND)
  }
  println(s"User generation finished($numberOfUsers)")

  val maxNumberOfCells = 11000
  val minNumberOfCells = 9000
  val numberOfCells = minNumberOfUsers + r.nextInt(maxNumberOfCells - minNumberOfCells)
  val cellsFilename = "geo-cells.tsv"
  val maxDistanceError = 20000
  println(s"Grid generation started ($numberOfCells)")
  val cellsPath: Path = Paths.get(cellsFilename)
  Files.write(cellsPath, Array.emptyByteArray, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
  val cells = (-179 to 180) flatMap { longitude =>
    (-90 to 90) map { latitude =>
      (longitude, latitude)
    }
  }
  r.shuffle(cells).take(numberOfCells) foreach { coordinates =>
    val str = s"${coordinates._1}\t${coordinates._2}\t${r.nextInt(maxDistanceError)}\n"
    Files.write(cellsPath, str.getBytes, StandardOpenOption.APPEND)
  }
  println(s"Grid generation finished ($numberOfCells)")

}
