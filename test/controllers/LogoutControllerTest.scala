package controllers

import models.UserDataRepository
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import akka.stream.Materializer

class LogoutControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite {

  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val logoutController = new LogoutController(userDataRepository, messagesApi)

  implicit lazy val materializer: Materializer = app.materializer

  "Logout Controller" should {

    "be able to log out successfully" in {
      val result = call(logoutController.logout(),FakeRequest(GET,"/logout")
        .withSession("userEmail" -> "divya@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "show error if user is not in session" in {
      val result = call(logoutController.logout(),FakeRequest(GET,"/logout"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }
  }

}
