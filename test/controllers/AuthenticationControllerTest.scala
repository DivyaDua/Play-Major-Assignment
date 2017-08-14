package controllers

import akka.stream.Materializer
import models.{UserDataModel, UserDataRepository}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.test.WithApplication

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class AuthenticationControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  /*val forms: UserForms = mock[UserForms]
  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]

  val authenticationController = new AuthenticationController(userDataRepository, forms, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Authentication Controller" should{

    "be able to create a user" in {

      val user = UserData("Divya", None,  "Dua", 21, "female", 8130212805L, "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(None))
      when(userDataRepository.store(UserDataModel(0, "Divya", None, "Dua", 21, "female", 8130212805L,"divya.dua@knoldus.in", "divyadua1")))
        .thenReturn(Future(true))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
      "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
      "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/profile")
    }
  }*/
}
