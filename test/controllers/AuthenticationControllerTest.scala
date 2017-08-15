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

class AuthenticationControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  val forms: UserForms = mock[UserForms]
  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userPlusHobbiesRepository: UserPlusHobbiesRepository = mock[UserPlusHobbiesRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val authenticationController = new AuthenticationController(userDataRepository, forms, hobbiesRepository,
    userPlusHobbiesRepository, messagesApi)

  val TWENTY_ONE = 21
  val NINTY = 90
  val NUMBER = 8130212805L
  val ONE = 1
  val TWO = 2
  val THREE = 3
  val FOUR = 4
  val FIVE = 5

  implicit lazy val materializer: Materializer = app.materializer

  "Authentication Controller" should {

    "be able to create a user" in {

      val user = UserData("Divya", None,  "Dua", TWENTY_ONE, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.retrieveUserId("divya.dua@knoldus.in")).thenReturn(Future(1))
      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(false))
      when(userDataRepository.store(ArgumentMatchers.any(classOf[UserDataModel])))
        .thenReturn(Future(true))
      when(userPlusHobbiesRepository.addUserHobbies(1, List(ONE,FIVE))).thenReturn(Future(true))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
      "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "not be able to create a user, failed to add hobbies to the database" in {

      val user = UserData("Divya", None,  "Dua", TWENTY_ONE, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.retrieveUserId("divya.dua@knoldus.in")).thenReturn(Future(1))
      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(false))
      when(userDataRepository.store(ArgumentMatchers.any(classOf[UserDataModel])))
        .thenReturn(Future(true))
      when(userPlusHobbiesRepository.addUserHobbies(1, List(ONE,FIVE))).thenReturn(Future(false))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
        "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to create a user as retrieved user id is not greater than 0" in {

      val user = UserData("Divya", None,  "Dua", TWENTY_ONE, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.retrieveUserId("divya.dua@knoldus.in")).thenReturn(Future(0))
      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(false))
      when(userDataRepository.store(ArgumentMatchers.any(classOf[UserDataModel])))
        .thenReturn(Future(true))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
        "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to store user data in table" in {

      val user = UserData("Divya", None,  "Dua", TWENTY_ONE, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(false))
      when(userDataRepository.store(ArgumentMatchers.any(classOf[UserDataModel])))
        .thenReturn(Future(false))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
        "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not be able to create user, email already exists" in {

      val user = UserData("Divya", None,  "Dua", TWENTY_ONE, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)

      when(userDataRepository.findByEmail("divya.dua@knoldus.in")).thenReturn(Future(true))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
        "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "21", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/login")
    }

    "not be able to create user, invalid fields in form" in {

      val user = UserData("Divya", None,  "Dua", NINTY, "female", NUMBER, List("dancing","watching tv"), "divya.dua@knoldus.in", "divyadua1", "divyadua1")
      val form = new UserForms().userForm.fill(user)
      when(forms.userForm).thenReturn(form)
      when(hobbiesRepository.retrieveHobbies).thenReturn(Future(List(HobbiesModel(ONE, "dancing"), HobbiesModel(TWO, "listening music"),
        HobbiesModel(THREE, "photography"), HobbiesModel(FOUR, "reading novels"), HobbiesModel(FIVE, "watching tv"))))

      val result = call(authenticationController.createUserPost(),FakeRequest(POST,"/home").withFormUrlEncodedBody(
        "firstName" -> "Divya", "middleName" -> "","lastName" -> "Dua", "age" -> "90", "gender" -> "female",
        "mobileNumber" -> "8130212805","hobbies[0]" -> "1", "hobbies[1]" -> "5",
        "email" -> "divya.dua@knoldus.in", "password" -> "divyadua1", "confirmPassword" -> "divyadua1"))

      status(result) mustBe 400
    }
  }
}
