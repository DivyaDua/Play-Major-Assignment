package models

import controllers.UserProfileData
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserDataRepositoryTest extends PlaySpec with MockitoSugar {

  val TWENTY_ONE = 21
  val NUMBER = 8130212805L
  val TRUE = true
  val FALSE = false

  val hashesPassword1: String = BCrypt.hashpw("divya12345", BCrypt.gensalt())
  val hashesPassword2: String = BCrypt.hashpw("neha12345", BCrypt.gensalt())
  val hashesPassword3: String = BCrypt.hashpw("shruti12345", BCrypt.gensalt())
  val hashesPassword4: String = BCrypt.hashpw("nehadua12345", BCrypt.gensalt())

  private val userDataModel1 = UserDataModel(0,"DivyaTest", None, "Dua", TWENTY_ONE, "female", NUMBER, "divya.dua@knoldus.in",
    hashesPassword1, TRUE, TRUE)
  private val userDataModel2 = UserDataModel(0,"NehaTest", None, "Dua", TWENTY_ONE, "female", NUMBER, "neha.dua@knoldus.in",
    hashesPassword2, FALSE, FALSE)
  private val userDataModel3 = UserDataModel(0,"ShrutiTest", None, "Gupta", TWENTY_ONE, "female", NUMBER, "shruti.gupta@knoldus.in",
    hashesPassword3, TRUE, FALSE)

  val userDataRepositoryModel = new ModelsTest[UserDataRepository]

  "User Data Repository" should {

    "be able to store user data in the table" in {
      val storeResult1 = userDataRepositoryModel.result(userDataRepositoryModel.repository.store(userDataModel1))
      val storeResult2 = userDataRepositoryModel.result(userDataRepositoryModel.repository.store(userDataModel2))
      val storeResult3 = userDataRepositoryModel.result(userDataRepositoryModel.repository.store(userDataModel3))

      storeResult1 must equal(true)
      storeResult2 must equal(true)
      storeResult3 must equal(true)
    }

    "be able to retrieve user information for a given email" in {
      val retrieveResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.retrieve("divya.dua@knoldus.in"))
      retrieveResult must equal(List(UserDataModel(1,"DivyaTest",None,"Dua",TWENTY_ONE,"female",
        NUMBER,"divya.dua@knoldus.in",hashesPassword1,TRUE,TRUE)))
    }

    "not be able to retrieve any user information for invalid email" in {
      val retrieveResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.retrieve("divya@knoldus.in"))
      retrieveResult must equal(List())
    }

    "be able to retrieve user ID for a given email" in {
      val retrieveResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.retrieveUserId("divya.dua@knoldus.in"))
      retrieveResult must equal(1)
    }

    "be able to retrieve user ID for invalid email" in {
      val retrieveResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.retrieveUserId("divya@knoldus.in"))
      retrieveResult must equal(0)
    }

    "be able to update user information for a given email" in {
      val userProfileData = UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER)
      val updatedResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.updateUserProfile(userProfileData, "divya.dua@knoldus.in"))
      updatedResult must equal(true)
    }

    "not be able to update user information for invalid email" in {
      val userProfileData = UserProfileData("Divya", None, "Dua", TWENTY_ONE, "female", NUMBER)
      val updatedResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.updateUserProfile(userProfileData, "divya@knoldus.in"))
      updatedResult must equal(false)
    }

    "be able to return true or false if user is enabled or not" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.checkIsEnabled("divya.dua@knoldus.in"))
      result must equal(Some(true))
    }

    "not return anything while checking for enabled if email is invalid" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.checkIsEnabled("divya@knoldus.in"))
      result must equal(None)
    }

    "be able to enable user" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.enableUser("neha.dua@knoldus.in"))
      result must equal(true)
    }

    "return false if attempt is made to enable a user who does not exist" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.enableUser("neha@knoldus.in"))
      result must equal(false)
    }

    "be able to disable user" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.disableUser("neha.dua@knoldus.in"))
      result must equal(true)
    }

    "return false if attempt is made to disable a user who does not exist" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.disableUser("neha@knoldus.in"))
      result must equal(false)
    }

    "be able to update password for valid user" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.updatePassword("neha.dua@knoldus.in", hashesPassword4))
      result must equal(true)
    }

    "not be able to update password for invalid user" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.updatePassword("neha@knoldus.in", hashesPassword4))
      result must equal(false)
    }

    "be able to return true or false if user is admin or not" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.checkIsAdmin("divya.dua@knoldus.in"))
      result must equal(Some(true))
    }

    "not return anything while checking for admin if email is invalid" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.checkIsAdmin("divya@knoldus.in"))
      result must equal(None)
    }

    "return true if email already exists" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.findByEmail("divya.dua@knoldus.in"))
      result must equal(true)
    }

    "return false if email does not exist" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.findByEmail("divya@knoldus.in"))
      result must equal(false)
    }

    "be able to retrieve all users who are not admin" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.retrieveAllUsers)
      result must equal(List(UserDataModel(3,"ShrutiTest", None, "Gupta", TWENTY_ONE, "female", NUMBER, "shruti.gupta@knoldus.in",
        hashesPassword3, TRUE, FALSE),UserDataModel(2,"NehaTest",None,"Dua",TWENTY_ONE,"female",
          NUMBER,"neha.dua@knoldus.in",hashesPassword4,FALSE,FALSE)))
    }

    "be able to validate password by returning true if it matches" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.validatePassword("divya.dua@knoldus.in", "divya12345"))
      result mustEqual true
    }

    "be able to validate password by returning false if it does not match" in {
      val result = userDataRepositoryModel.result(userDataRepositoryModel.repository.validatePassword("divya.dua@knoldus.in", "divya1234"))
      result mustEqual false
    }


  }

}
