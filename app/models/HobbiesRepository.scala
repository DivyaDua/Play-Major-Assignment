package models

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class HobbiesModel(hobbyId: Int, hobby: String)

class HobbiesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbiesRepositoryTable{

  import driver.api._
  def retrieveHobbies: Future[List[String]] = {
    val query = hobbiesTable.map(_.hobby).to[List].result
    db.run(query)
  }

  def retrieveHobbiesID(hobbies: List[String]): Future[List[Int]] = {
    val listOfFutureHobbyIds = hobbies.map(hobby => db.run(hobbiesTable.filter(_.hobby === hobby).map(_.hobbyId).to[List].result))
    Future.sequence(listOfFutureHobbyIds).map(_.flatten)
  }

}

trait HobbiesRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  val hobbiesTable: TableQuery[HobbiesTable] = TableQuery[HobbiesTable]

  class HobbiesTable(tag: Tag) extends Table[HobbiesModel](tag, "hobbiestable") {
    def hobbyId: Rep[Int] = column[Int]("hid", O.AutoInc, O.PrimaryKey)
    def hobby: Rep[String] = column[String]("hobby")

    def * : ProvenShape[HobbiesModel] = (hobbyId, hobby) <> (HobbiesModel.tupled,
      HobbiesModel.unapply)
  }

}