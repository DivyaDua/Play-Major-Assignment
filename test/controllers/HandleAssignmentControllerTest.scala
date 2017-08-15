package controllers

import models.{AssignmentModel, AssignmentRepository, UserDataRepository}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.stream.Materializer

class HandleAssignmentControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val assignmentRepository: AssignmentRepository = mock[AssignmentRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val forms: AssignmentForm = mock[AssignmentForm]
  val handleAssignmentController = new HandleAssignmentController(userDataRepository, assignmentRepository, forms, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Handle Assignment Controller" should {

    "be able to delete assignment" in {
      when(assignmentRepository.delete(1)).thenReturn(Future(true))
      val result = call(handleAssignmentController.deleteAssignment(1),FakeRequest(GET,"/deleteAssignment")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewAssignment")
    }

    "not be able to delete assignment whose id is not there in assignment repository" in {
      when(assignmentRepository.delete(1)).thenReturn(Future(false))
      val result = call(handleAssignmentController.deleteAssignment(1),FakeRequest(GET,"/deleteAssignment")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewAssignment")
    }

    "not be able to delete assignment when admin is not in session" in {
      val result = call(handleAssignmentController.deleteAssignment(1),FakeRequest(GET,"/deleteAssignment"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "be able to add assignment in the table" in {
      val assignment: Assignment = Assignment("Play", "Create a user login applicaton")
      val form = new AssignmentForm().assignmentForm.fill(assignment)
      when(forms.assignmentForm).thenReturn(form)

      when(assignmentRepository.addAssignment(AssignmentModel(0,"Play", "Create a user login applicaton")))
        .thenReturn(Future(true))
      val result = call(handleAssignmentController.addAssignment(),FakeRequest(POST,"/addAssignment")
        .withFormUrlEncodedBody("title" -> "Play", "description" -> "Create a user login applicaton")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewAssignment")
    }

    "fail to add assignment in the table" in {
      val assignment: Assignment = Assignment("Play", "Create a user login applicaton")
      val form = new AssignmentForm().assignmentForm.fill(assignment)
      when(forms.assignmentForm).thenReturn(form)

      when(assignmentRepository.addAssignment(AssignmentModel(0,"Play", "Create a user login application")))
        .thenReturn(Future(false))
      val result = call(handleAssignmentController.addAssignment(),FakeRequest(POST,"/addAssignment")
        .withFormUrlEncodedBody("title" -> "Play", "description" -> "Create a user login application")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewAssignment")
    }

    "fail to add assignment when admin is not in session" in {
      val result = call(handleAssignmentController.addAssignment(),FakeRequest(POST,"/addAssignment")
        .withFormUrlEncodedBody("title" -> "Play", "description" -> "Create a user login application"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    /*"fail to add assignment when form with errors come" in {
      val assignment: Assignment = Assignment("Play", "")
      val form = new AssignmentForm().assignmentForm.fill(assignment)
      when(forms.assignmentForm).thenReturn(form)

      val result = call(handleAssignmentController.addAssignment(),FakeRequest(POST,"/addAssignment")
        .withFormUrlEncodedBody("title" -> "Play").withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 400
    }*/
  }

}
