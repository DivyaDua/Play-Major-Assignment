package models

import javax.inject.Inject
import controllers.UserProfileData
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class UserDataModel(id: Int, firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, mobileNumber: Long, email: String, password: String, isEnabled: Boolean = true, isAdmin: Boolean = false)

class UserDataRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserDataRepositoryTable {

  import driver.api._
  def store(userDataModel: UserDataModel): Future[Boolean] = {
    db.run(userDataTable += userDataModel).map(_ > 0)
  }

  def retrieve(email: String): Future[List[UserDataModel]] = {
    val query = userDataTable.filter(_.email === email).to[List].result
    db.run(query)
  }

  def retrieveUserId(email: String): Future[Int] = {
    val query = userDataTable.filter(_.email === email).map(_.id).to[List].result.headOption
    db.run(query).map{
      case Some(id) => id
      case None => 0
    }
  }

  def updateUserProfile(userProfile: UserProfileData, email: String): Future[Boolean] = {
    val query = userDataTable.filter(_.email === email).map(e => (e.firstName, e.middleName, e.lastName,
      e.age, e.gender, e.mobileNumber, e.email)).update(userProfile.firstName, userProfile.middleName,
      userProfile.lastName, userProfile.age, userProfile.gender, userProfile.mobileNumber, userProfile.email)
    db.run(query).map(_ > 0)
  }

  def checkIsEnabled(email: String): Future[Boolean] = {
    val query = userDataTable.filter(_.email === email).map(_.isEnabled).result.headOption
    db.run(query).map{
      case Some(bool) if bool => true
      case Some(bool) if !bool => false
      case None => false
    }
  }

  def updatePassword(email: String, password: String): Future[Boolean] = {
    val query = userDataTable.filter(_.email === email).map(e => e.password).update(password)
    db.run(query).map(_ > 0)
  }

  def retrieveNameAndEmail: Future[List[(String, String)]] = {
    val query = userDataTable.filter(_.isAdmin === false).map(e => (e.firstName, e.email)).to[List].result
    db.run(query)
  }

  def checkIsAdmin(email: String): Future[Boolean] = {
    val query = userDataTable.filter(_.email === email).map(_.isAdmin).result.headOption
    db.run(query).map{
      case Some(bool) if bool => true
      case Some(bool) if !bool => false
      case None => false
    }
  }

  def findByEmail(email: String): Future[Boolean] = {
    val query = userDataTable.filter(_.email === email).map(_.email).result.headOption
    db.run(query).map{
      case Some(_) => true
      case None => false
    }
  }

  def validatePassword(email: String, password: String): Future[Boolean] = {
    Logger.info("Checking if user exists in Database")
    val query = userDataTable.filter(_.email === email).to[List].result
    db.run(query).map { user =>
      if (user.isEmpty) {
        false
      }
      else if (!BCrypt.checkpw(password, user.head.password)) {
        false
      }
      else {
        true
      }
    }
  }
}

trait UserDataRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  val userDataTable: TableQuery[UserDataTable] = TableQuery[UserDataTable]

  class UserDataTable(tag: Tag) extends Table[UserDataModel](tag, "userdatatable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def firstName: Rep[String] = column[String]("firstname")
    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")
    def lastName: Rep[String] = column[String]("lastname")
    def age: Rep[Int] = column[Int]("age")
    def gender: Rep[String] = column[String]("gender")
    def mobileNumber: Rep[Long] = column[Long]("mobilenumber")
    def email: Rep[String] = column[String]("email", O.PrimaryKey)
    def password: Rep[String] = column[String]("password")
    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")
    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def * : ProvenShape[UserDataModel] = (id, firstName, middleName, lastName, age, gender,
      mobileNumber, email, password, isEnabled, isAdmin) <> (UserDataModel.tupled,
      UserDataModel.unapply)
  }

}
