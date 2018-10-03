package onefactor.user

case class User(id: String,
                lon: Double,
                lat: Double,
                isNextToLabel: Option[Boolean])

case class Users(tileX: Int,
                 tileY: Int,
                 count: Int)
