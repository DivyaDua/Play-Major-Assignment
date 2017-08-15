package models

import javax.inject.Inject

import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AssignmentModel(id: Int, title: String, description: String)

class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AssignmentRepositoryTable{

  import driver.api._

  def addAssignment(assignmentModel: AssignmentModel): Future[Boolean] = {
    Logger.info("Adding assignment to the assignment table")
    db.run(assignmentTable += assignmentModel).map(_ > 0)
  }

  def delete(id: Int): Future[Boolean] = {
    Logger.info(s"Deleting assignment with $id")
    val query = assignmentTable.filter(_.id === id).delete
    db.run(query).map(_ > 0)
  }

  def retrieveAssignments: Future[List[AssignmentModel]] = {
    Logger.info("Retrieving assignments")
    val query = assignmentTable.to[List].result
    db.run(query)
  }

}

trait AssignmentRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  val assignmentTable: TableQuery[AssignmentTable] = TableQuery[AssignmentTable]

  class AssignmentTable(tag: Tag) extends Table[AssignmentModel](tag, "assignmenttable") {
    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    def title: Rep[String] = column[String]("title")
    def description: Rep[String] = column[String]("description")

    def * : ProvenShape[AssignmentModel] = (id, title, description) <> (AssignmentModel.tupled,
      AssignmentModel.unapply)
  }

}
