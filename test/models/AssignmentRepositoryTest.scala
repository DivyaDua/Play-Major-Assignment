package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class AssignmentRepositoryTest extends PlaySpec with MockitoSugar {

  val assignmentRepositoryModel = new ModelsTest[AssignmentRepository]
  val assignmentModel = AssignmentModel(1, "Play", "Create user login application")

  "Assignment repository" should {
    "be able to add assignment" in {
      val storedresult = assignmentRepositoryModel.result(assignmentRepositoryModel.repository.addAssignment(assignmentModel))
      storedresult mustEqual true
    }

    "be able to retrieve all assignment" in {
      val retrievedResult = assignmentRepositoryModel.result(assignmentRepositoryModel.repository.retrieveAssignments)
      retrievedResult mustEqual List(assignmentModel)
    }

    "be able to delete assignment" in {
      val result = assignmentRepositoryModel.result(assignmentRepositoryModel.repository.delete(1))
      result mustEqual true
    }

    "return false if there is no assignment with given id" in {
      val result = assignmentRepositoryModel.result(assignmentRepositoryModel.repository.delete(2))
      result mustEqual false
    }
  }

}
