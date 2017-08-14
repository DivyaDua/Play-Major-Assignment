package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.{ProvenShape, QueryBase}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserHobbiesModel(id: Int, userId: Int, hobbyId: Int)

class UserPlusHobbiesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserPlusHobbiesRepositoryTable with HobbiesRepositoryTable{

  import driver.api._

  def addUserHobbies(userId: Int, hobbiesIdList: List[Int]): Future[Boolean] = {
    val listOfValidHobbies = hobbiesIdList.filter(_ != Nil)
    val listOfResult: List[Future[Boolean]] = listOfValidHobbies.map (
      hobbyID => db.run(userHobbiesTable += UserHobbiesModel(0, userId, hobbyID)).map(_ > 0)
    )
    Future.sequence(listOfResult).map {
      result =>
        if (result.contains(false)) false else true
    }
  }

  def updateUserHobbies(uid: Int, hobbiesIdList: List[Int]): Future[Boolean] = {
    db.run(userHobbiesTable.filter(_.userId === uid).delete).map(_ > 0)
    addUserHobbies(uid, hobbiesIdList)
  }

  def getUserHobby(uid: Int): Future[List[String]] = {
    val userAndHobbyJoin: QueryBase[Seq[(Int, String)]] = for{
      (user,hobby) <- userHobbiesTable join hobbiesTable on (_.hobbyId === _.hobbyId)
    } yield (user.userId, hobby.hobby)

    val queryBase = userAndHobbyJoin.result
    db.run(queryBase).map(userAndHobby => userAndHobby.filter(_._1 == uid).map(_._2).toList)
  }
}

trait UserPlusHobbiesRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  val userHobbiesTable: TableQuery[UserHobbiesTable] = TableQuery[UserHobbiesTable]

  class UserHobbiesTable(tag: Tag) extends Table[UserHobbiesModel](tag, "userhobbiestable") {
    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def userId: Rep[Int] = column[Int]("userid")
    def hobbyId: Rep[Int] = column[Int]("hid")

    def * : ProvenShape[UserHobbiesModel] = (id, userId, hobbyId) <> (UserHobbiesModel.tupled,
      UserHobbiesModel.unapply)
  }

}