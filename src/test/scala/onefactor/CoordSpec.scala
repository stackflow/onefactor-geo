package onefactor

import org.scalatest.{FlatSpec, Matchers}

class CoordSpec extends FlatSpec with Matchers {

  "Coord check" should "return false for wrong coords" in {
    Coord.isRight(-190, 40) should equal(false)
    Coord.isRight(200, 40) should equal(false)
    Coord.isRight(59, -140) should equal(false)
    Coord.isRight(59, 140) should equal(false)
    Coord.isRight(259, 140) should equal(false)
    Coord.isRight(-259, 140) should equal(false)
    Coord.isRight(-259, -140) should equal(false)
  }

  it should "return true for right coords" in {
    Coord.isRight(59, 59) should equal(true)
    Coord.isRight(-59, 90) should equal(true)
    Coord.isRight(90, -30) should equal(true)
    Coord.isRight(-49, -90) should equal(true)
  }

}