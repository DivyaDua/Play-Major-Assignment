package controllers

import models.{HobbiesModel, HobbiesRepository}
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

class ApplicationTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val forms: UserForms = mock[UserForms]
  val assignmentForm: AssignmentForm = mock[AssignmentForm]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val application = new Application(messagesApi, forms, assignmentForm, hobbiesRepository)

  implicit lazy val materializer: Materializer = app.materializer

  "Application" should {

    "be able to display welcome page" in {
      val result = call(application.index1(),FakeRequest(GET,"/"))
      status(result) mustBe 200
    }

    "be able to display forgot password page" in {
      val form = new UserForms().userForgotPasswordForm
      when(forms.userForgotPasswordForm).thenReturn(form)

      val result = call(application.showForgotPasswordPage(),FakeRequest(GET,"/forgotPasswordPage"))
      status(result) mustBe 200
    }

    "be able to display add assignment page" in {
      val form = new AssignmentForm().assignmentForm
      when(assignmentForm.assignmentForm).thenReturn(form)

      val result = call(application.showAddAssignmentPage(),FakeRequest(GET,"/addAssignmentPage"))
      status(result) mustBe 200
    }

    "be able to show registration page" in {
      when(hobbiesRepository.retrieveHobbies).thenReturn(Future(List(HobbiesModel(1, "dancing"))))
      val form = new UserForms().userForm
      when(forms.userForm).thenReturn(form)

      val result = call(application.showRegistrationPage(),FakeRequest(GET,"/registration"))
      status(result) mustBe 200
    }

    "be able to display login page" in {
      val form = new UserForms().userLoginForm
      when(forms.userLoginForm).thenReturn(form)

      val result = call(application.showLoginPage(),FakeRequest(GET,"/forgotPasswordPage").withFlash("hello" -> "user"))
      status(result) mustBe 200
    }

  }

}
