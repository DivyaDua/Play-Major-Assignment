package controllers

import akka.stream.Materializer
import models.{HobbiesRepository, UserDataModel, UserDataRepository, UserPlusHobbiesRepository}
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

class ViewUserControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val viewUserController = new ViewUserController(userDataRepository, messagesApi)
  val TWENTY_ONE = 21
  val NUMBER = 8130212805L
  implicit lazy val materializer: Materializer = app.materializer

  "View User Controller" should {

    "be able to view all the users" in {
      when(userDataRepository.retrieveAllUsers).thenReturn(Future(List(UserDataModel(1, "Divya", None, "Dua",
        TWENTY_ONE, "female", NUMBER, "divya.dua@knoldus.in", "divyadua1"))))

      val result = call(viewUserController.viewUsers(),FakeRequest(GET,"/viewUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))
      status(result) mustBe 200
    }

    "show nothing when the admin is not in session and redirect to welcome page" in {
        val result = call(viewUserController.viewUsers(),FakeRequest(GET,"/viewUser"))

        status(result) mustBe 303
        redirectLocation(result) mustBe Some("/")
    }

    "show no user found by redirecting on page when there are no users" in {
      when(userDataRepository.retrieveAllUsers).thenReturn(Future(Nil))
      val result = call(viewUserController.viewUsers(),FakeRequest(GET,"/viewUser")
        .withSession("userEmail" -> "divyaduamzn12@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }
  }

}
