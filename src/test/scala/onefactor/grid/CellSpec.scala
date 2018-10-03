package onefactor.grid

import org.scalatest._

class CellSpec extends FlatSpec with Matchers {

  "Cell" should "evaluate right distance" in {
    val cell = Cell(45, 69, 120000)
    cell.distance(45, 69) should equal(0)
  }

  it should "evaluate right next to label" in {
    val cell = Cell(45, 69, 120000)
    cell.isNextToLabel(45.1, 69.3) should equal(true)
  }

}
