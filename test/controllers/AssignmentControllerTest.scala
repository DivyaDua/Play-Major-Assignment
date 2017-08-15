package controllers

import models._
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

class AssignmentControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite  {

  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val assignmentRepository: AssignmentRepository = mock[AssignmentRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val assignmentController = new AssignmentController(userDataRepository, assignmentRepository, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Assignment Controller" should{
    "be able to show assignment to the admin" in {
      when(assignmentRepository.retrieveAssignments)
        .thenReturn(Future(List(AssignmentModel(1, "Play", "Create user login application"))))
      when(userDataRepository.checkIsAdmin("divya@gmail.com")).thenReturn(Future(Some(true)))

      val result = call(assignmentController.viewAssignment(), FakeRequest(GET, "/viewAssignment")
        .withSession("userEmail" -> "divya@gmail.com"))

      status(result) mustBe 200
    }

    "be able to show assignment to the user" in {
      when(assignmentRepository.retrieveAssignments)
        .thenReturn(Future(List(AssignmentModel(1, "Play", "Create user login application"))))
      when(userDataRepository.checkIsAdmin("divya@gmail.com")).thenReturn(Future(Some(false)))

      val result = call(assignmentController.viewAssignment(), FakeRequest(GET, "/viewAssignment")
        .withSession("userEmail" -> "divya@gmail.com"))

      status(result) mustBe 200
    }

    "be able to show assignment to the users if user does not exist" in {
      when(assignmentRepository.retrieveAssignments)
        .thenReturn(Future(List(AssignmentModel(1, "Play", "Create user login application"))))
      when(userDataRepository.checkIsAdmin("divya@gmail.com")).thenReturn(Future(None))

      val result = call(assignmentController.viewAssignment(), FakeRequest(GET, "/viewAssignment")
        .withSession("userEmail" -> "divya@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "be able to show assignment to the users if not in session" in {

     val result = call(assignmentController.viewAssignment(), FakeRequest(GET, "/viewAssignment"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }
  }
}
