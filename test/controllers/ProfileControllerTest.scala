package controllers

import akka.stream.Materializer
import org.mockito.Mockito._
import models._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProfileControllerTest extends PlaySpec with MockitoSugar with GuiceOneServerPerSuite{

  val forms: UserForms = mock[UserForms]
  val userDataRepository: UserDataRepository = mock[UserDataRepository]
  val hobbiesRepository: HobbiesRepository = mock[HobbiesRepository]
  val userPlusHobbiesRepository: UserPlusHobbiesRepository = mock[UserPlusHobbiesRepository]
  val messagesApi: MessagesApi = mock[MessagesApi]
  val profileController = new ProfileController(userDataRepository, forms,
    userPlusHobbiesRepository, hobbiesRepository, messagesApi)

  val TWENTY_ONE = 21
  val NUMBER = 8130212805L
  val ONE = 1
  val TWO = 2
  val THREE = 3
  val FOUR = 4
  val FIVE = 5

  implicit lazy val materializer: Materializer = app.materializer

  "Profile controller" should {

    "be able to show user profile" in {
      when(userDataRepository.retrieve("divyadua@gmail.com")).thenReturn(Future(List(UserDataModel(1, "Divya", None, "Dua",
        TWENTY_ONE, "female", NUMBER, "divya.dua@knoldus.in", "divyadua1"))))

      when(userPlusHobbiesRepository.getUserHobby(1)).thenReturn(Future(List(ONE, FIVE)))
      when(hobbiesRepository.retrieveHobbies).thenReturn(Future(List(HobbiesModel(ONE, "dancing"), HobbiesModel(TWO, "listening music"),
        HobbiesModel(THREE, "photography"), HobbiesModel(FOUR, "reading novels"), HobbiesModel(FIVE, "watching tv"))))

      val form = new UserForms().userProfileForm
      when(forms.userProfileForm).thenReturn(form)

      val result = call(profileController.showUserProfile(),FakeRequest(GET,"/showProfile")
        .withSession("userEmail" -> "divyadua@gmail.com"))

      status(result) mustBe 200
    }

    "not show user profile when hobbies are not retrieved" in {
      when(userDataRepository.retrieve("divyadua@gmail.com")).thenReturn(Future(List(UserDataModel(1, "Divya", None, "Dua",
        TWENTY_ONE, "female", NUMBER, "divya.dua@knoldus.in", "divyadua1"))))

      when(userPlusHobbiesRepository.getUserHobby(1)).thenReturn(Future(Nil))

      val form = new UserForms().userProfileForm
      when(forms.userProfileForm).thenReturn(form)

      val result = call(profileController.showUserProfile(),FakeRequest(GET,"/showProfile")
        .withSession("userEmail" -> "divyadua@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not show user profile when user email does not match" in {
      when(userDataRepository.retrieve("divyadua@gmail.com")).thenReturn(Future(Nil))

      val form = new UserForms().userProfileForm
      when(forms.userProfileForm).thenReturn(form)

      val result = call(profileController.showUserProfile(),FakeRequest(GET,"/showProfile")
        .withSession("userEmail" -> "divyadua@gmail.com"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "not show user profile when user is not in session" in {
      val result = call(profileController.showUserProfile(),FakeRequest(GET,"/showProfile"))
      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }

    "be able to update profile" in {
      val userProfile = UserProfile("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER, List(ONE, FIVE))
      val form = new UserForms().userProfileForm.fill(userProfile)
      when(forms.userProfileForm).thenReturn(form)
      when(userDataRepository.updateUserProfile(UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER), "divyadua@gmail.com"))
        .thenReturn(Future(true))
      when(userDataRepository.retrieveUserId("divyadua@gmail.com")).thenReturn(Future(ONE))
      when(userPlusHobbiesRepository.updateUserHobbies(ONE, List(ONE, FIVE))).thenReturn(Future(true))

      val result = call(profileController.updateProfile(),FakeRequest(POST,"/updateProfile")
          .withFormUrlEncodedBody("firstName" -> "Divya", "middleName" -> "", "lastName" -> "Dua", "age" -> "21",
          "gender" -> "female", "mobileNumber" -> "8130212805", "hobbies[0]"-> "1", "hobbies[1]" -> "5")
        .withSession("userEmail" -> "divyadua@gmail.com", "isAdmin" -> "true"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "fail to update user hobbies" in {
      val userProfile = UserProfile("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER, List(ONE, FIVE))
      val form = new UserForms().userProfileForm.fill(userProfile)
      when(forms.userProfileForm).thenReturn(form)
      when(userDataRepository.updateUserProfile(UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER), "divyadua@gmail.com"))
        .thenReturn(Future(true))
      when(userDataRepository.retrieveUserId("divyadua@gmail.com")).thenReturn(Future(ONE))
      when(userPlusHobbiesRepository.updateUserHobbies(ONE, List(ONE, FIVE))).thenReturn(Future(false))

      val result = call(profileController.updateProfile(),FakeRequest(POST,"/updateProfile")
        .withFormUrlEncodedBody("firstName" -> "Divya", "middleName" -> "", "lastName" -> "Dua", "age" -> "21",
          "gender" -> "female", "mobileNumber" -> "8130212805", "hobbies[0]"-> "1", "hobbies[1]" -> "5")
        .withSession("userEmail" -> "divyadua@gmail.com", "isAdmin" -> "true"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "fail to update user profile because no user id is returned" in {
      val userProfile = UserProfile("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER, List(ONE, FIVE))
      val form = new UserForms().userProfileForm.fill(userProfile)
      when(forms.userProfileForm).thenReturn(form)
      when(userDataRepository.updateUserProfile(UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER), "divyadua@gmail.com"))
        .thenReturn(Future(true))
      when(userDataRepository.retrieveUserId("divyadua@gmail.com")).thenReturn(Future(0))

      val result = call(profileController.updateProfile(),FakeRequest(POST,"/updateProfile")
        .withFormUrlEncodedBody("firstName" -> "Divya", "middleName" -> "", "lastName" -> "Dua", "age" -> "21",
          "gender" -> "female", "mobileNumber" -> "8130212805", "hobbies[0]"-> "1", "hobbies[1]" -> "5")
        .withSession("userEmail" -> "divyadua@gmail.com", "isAdmin" -> "true"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "fail to update user profile data" in {
      val userProfile = UserProfile("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER, List(ONE, FIVE))
      val form = new UserForms().userProfileForm.fill(userProfile)
      when(forms.userProfileForm).thenReturn(form)
      when(userDataRepository.updateUserProfile(UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER), "divyadua@gmail.com"))
        .thenReturn(Future(false))

      val result = call(profileController.updateProfile(),FakeRequest(POST,"/updateProfile")
        .withFormUrlEncodedBody("firstName" -> "Divya", "middleName" -> "", "lastName" -> "Dua", "age" -> "21",
          "gender" -> "female", "mobileNumber" -> "8130212805", "hobbies[0]"-> "1", "hobbies[1]" -> "5")
        .withSession("userEmail" -> "divyadua@gmail.com", "isAdmin" -> "true"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/showProfile")
    }

    "fail to update user profile data when user is not in session" in {
      val result = call(profileController.updateProfile(),FakeRequest(POST,"/updateProfile")
        .withFormUrlEncodedBody("firstName" -> "Divya", "middleName" -> "", "lastName" -> "Dua", "age" -> "21",
          "gender" -> "female", "mobileNumber" -> "8130212805", "hobbies[0]"-> "1", "hobbies[1]" -> "5"))

      status(result) mustBe 303
      redirectLocation(result) mustBe Some("/")
    }
  }

}
