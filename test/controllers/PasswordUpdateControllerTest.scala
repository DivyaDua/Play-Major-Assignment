package controllers

import akka.stream.Materializer
import models.{HobbiesRepository, UserDataRepository, UserPlusHobbiesRepository}
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

class PasswordUpdateControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  val forms: UserForms = mock[UserForms]
  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userPlusHobbiesRepository: UserPlusHobbiesRepository = mock[UserPlusHobbiesRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val passwordUpdateController = new PasswordUpdateController(userDataRepository, forms, hobbiesRepository,
    userPlusHobbiesRepository, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Password Update Controller" should{

    "be able to update password for user who is enabled" in {
      val userForgotPasswordData = UserForgotPasswordData("divya@gmail.com", "divya1234", "divya1234")
      val form = new UserForms().userForgotPasswordForm.fill(userForgotPasswordData)
      when(forms.userForgotPasswordForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya@gmail.com")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya@gmail.com")).thenReturn(Future(Some(true)))
      when(userDataRepository.updatePassword(ArgumentMatchers.any(classOf[String]), ArgumentMatchers.any(classOf[String]))).thenReturn(Future(true))

      val result = call(passwordUpdateController.updatePassword(),FakeRequest(POST,"/forgotPassword")
        .withFormUrlEncodedBody("email" -> "divya@gmail.com", "newPassword" -> "divya1234", "confirmPassword" -> "divya1234"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "fail to update password" in {
      val userForgotPasswordData = UserForgotPasswordData("divya@gmail.com", "divya1234", "divya1234")
      val form = new UserForms().userForgotPasswordForm.fill(userForgotPasswordData)
      when(forms.userForgotPasswordForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya@gmail.com")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya@gmail.com")).thenReturn(Future(Some(true)))
      when(userDataRepository.updatePassword(ArgumentMatchers.any(classOf[String]), ArgumentMatchers.any(classOf[String]))).thenReturn(Future(false))

      val result = call(passwordUpdateController.updatePassword(),FakeRequest(POST,"/forgotPassword")
        .withFormUrlEncodedBody("email" -> "divya@gmail.com", "newPassword" -> "divya1234", "confirmPassword" -> "divya1234"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/forgotPasswordPage")
    }

    "not be able to update password for user who is disabled" in {
      val userForgotPasswordData = UserForgotPasswordData("divya@gmail.com", "divya1234", "divya1234")
      val form = new UserForms().userForgotPasswordForm.fill(userForgotPasswordData)
      when(forms.userForgotPasswordForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya@gmail.com")).thenReturn(Future(true))
      when(userDataRepository.checkIsEnabled("divya@gmail.com")).thenReturn(Future(Some(false)))

      val result = call(passwordUpdateController.updatePassword(),FakeRequest(POST,"/forgotPassword")
        .withFormUrlEncodedBody("email" -> "divya@gmail.com", "newPassword" -> "divya1234", "confirmPassword" -> "divya1234"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to update password for user whose email does not match" in {
      val userForgotPasswordData = UserForgotPasswordData("divya@gmail.com", "divya1234", "divya1234")
      val form = new UserForms().userForgotPasswordForm.fill(userForgotPasswordData)
      when(forms.userForgotPasswordForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya@gmail.com")).thenReturn(Future(false))

      val result = call(passwordUpdateController.updatePassword(),FakeRequest(POST,"/forgotPassword")
        .withFormUrlEncodedBody("email" -> "divya@gmail.com", "newPassword" -> "divya1234", "confirmPassword" -> "divya1234"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

  }

}
