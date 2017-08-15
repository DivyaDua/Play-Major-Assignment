package controllers

import akka.stream.Materializer
import models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  val forms: UserForms = mock[UserForms]
  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userPlusHobbiesRepository: UserPlusHobbiesRepository = mock[UserPlusHobbiesRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val loginController = new LoginController(userDataRepository, forms, hobbiesRepository,
    userPlusHobbiesRepository, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Login Controller" should {

    "be able to log in successfully to show user profile" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua1")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(true))
      when(userDataRepository.validatePassword("divya.dua@knoldus.in", "divyadua1")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya.dua@knoldus.in")).thenReturn(Future(Some(true)))

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "not be able to log in when user is disabled" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua1")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(true))
      when(userDataRepository.validatePassword("divya.dua@knoldus.in", "divyadua1")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya.dua@knoldus.in")).thenReturn(Future(Some(false)))

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to log in when checkIsEnabled return nothing" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua1")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(true))
      when(userDataRepository.validatePassword("divya.dua@knoldus.in", "divyadua1")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya.dua@knoldus.in")).thenReturn(Future(None))

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to log in when password is incorrect" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua1")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(true))
      when(userDataRepository.validatePassword("divya.dua@knoldus.in", "divyadua1")).thenReturn(Future(false))

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "not be able to log in when email does not match" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua1")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(false))

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

   /* "not be able to log in when invalid values are entered by user" in {
      val user = UserLoginData("divya.dua@knoldus.in", "divyadua")
      val form = new UserForms().userLoginForm.fill(user)
      when(forms.userLoginForm).thenReturn(form)

      val result = call(loginController.createLoginPost(), FakeRequest(POST, "/login")
        .withFormUrlEncodedBody("email" -> "divya.dua@knoldus.in", "password" -> "divyadua"))

      status(result) mustBe 200
    }*/
  }



}
