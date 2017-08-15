package controllers

import models.UserDataRepository
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

class EnableDisableControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val enableDisableController = new EnableDisableController(userDataRepository, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Enable Disable Controller" should {

    "be able to enable user" in {
      when(userDataRepository.enableUser("divya.dua@knoldus.in")).thenReturn(Future(true))
      val result = call(enableDisableController.enableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/enableUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewUser")
    }

    "not be able to enable user with invalid email" in {
      when(userDataRepository.enableUser("divya.dua@knoldus.in")).thenReturn(Future(false))
      val result = call(enableDisableController.enableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/enableUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewUser")
    }

    "not be able to enable user when the admin is not in session" in {
      val result = call(enableDisableController.enableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/enableUser"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "be able to disable user" in {
      when(userDataRepository.disableUser("divya.dua@knoldus.in")).thenReturn(Future(true))
      val result = call(enableDisableController.disableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/disableUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewUser")
    }

    "not be able to disable user with invalid email" in {
      when(userDataRepository.disableUser("divya.dua@knoldus.in")).thenReturn(Future(false))
      val result = call(enableDisableController.disableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/disableUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/viewUser")
    }

    "not be able to disable user when the admin is not in session" in {
      val result = call(enableDisableController.disableUser("Divya", "divya.dua@knoldus.in"),FakeRequest(GET,"/disableUser"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }
  }


}
