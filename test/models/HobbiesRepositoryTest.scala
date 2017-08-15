package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class HobbiesRepositoryTest extends PlaySpec with MockitoSugar {

  val hobbiesRepositoryModel = new ModelsTest[HobbiesRepository]

  "Hobbies Repository" should {
    "be able to retrieve all hobbies with their ids"
  }

}
