package models

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserDataRepositoryTest extends PlaySpec with MockitoSugar {

  val boolTrue = true
  val boolFalse = false
  val userDataModel = UserDataModel(0,"DivyaTest", None, "Dua", 21, "female", 8130212805L, "divya.dua@knoldus,in",
    "divya12345", boolTrue, boolFalse)

  val userDataRepositoryModel = new ModelsTest[UserDataRepository]

  "User Data Repository" should {
    "be able to store user data in the table" in {
      val storeResult = userDataRepositoryModel.result(userDataRepositoryModel.repository.store(userDataModel))

      storeResult must equal(true)

    }
  }

}
