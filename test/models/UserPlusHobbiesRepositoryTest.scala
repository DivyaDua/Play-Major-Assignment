package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserPlusHobbiesRepositoryTest extends PlaySpec with MockitoSugar {

  val userPlusHobbiesRepositoryModel = new ModelsTest[UserPlusHobbiesRepository]

  val ONE = 1
  val TWO = 2
  val THREE = 3
  val FOUR = 4
  val FIVE = 5

  "User plus hobbies repository" should {
    "be able to add user hobbies" in {
      val storeResult = userPlusHobbiesRepositoryModel.result(userPlusHobbiesRepositoryModel.repository.addUserHobbies(1, List(ONE, FIVE)))
      storeResult mustEqual true
    }

    "be able to update user hobbies" in {
      val result = userPlusHobbiesRepositoryModel.result(userPlusHobbiesRepositoryModel.repository.updateUserHobbies(1, List(TWO, FIVE)))
      result mustEqual true
    }

    "be able to get user hobbies' id by taking in user id" in {
      val result = userPlusHobbiesRepositoryModel.result(userPlusHobbiesRepositoryModel.repository.getUserHobby(1))
      result mustEqual List(TWO, FIVE)
    }
  }

}
