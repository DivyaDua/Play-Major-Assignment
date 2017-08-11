package models

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserDataModel(id: Int, firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, email: String, password: String)

class UserDataRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserDataRepositoryTable {

  import driver.api._
  def store(userDataModel: UserDataModel): Future[Boolean] = {
    db.run(userDataTable += userDataModel).map(_ > 0)
  }

  def findByEmail(email: String): Future[Option[String]] = {
    val query = userDataTable.filter(_.email === email).map(_.email).result.headOption
    db.run(query)
  }
}

trait UserDataRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userDataTable: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserDataModel](tag, "userdatatable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc)
    def firstName: Rep[String] = column[String]("firstname")
    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")
    def lastName: Rep[String] = column[String]("lastname")
    def age: Rep[Int] = column[Int]("age")
    def gender: Rep[String] = column[String]("gender")
    def email: Rep[String] = column[String]("email")
    def password: Rep[String] = column[String]("password")

    def pk = primaryKey("pk_a", (id, email))

    def * : ProvenShape[UserDataModel] = (id, firstName, middleName, lastName, age, gender, email, password) <> (UserDataModel.tupled,
      UserDataModel.unapply)
  }

}
