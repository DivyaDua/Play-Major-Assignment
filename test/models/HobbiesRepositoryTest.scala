package models

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class HobbiesRepositoryTest extends PlaySpec with MockitoSugar {

  val hobbiesRepositoryModel = new ModelsTest[HobbiesRepository]
  val ONE = 1
  val TWO = 2
  val THREE = 3
  val FOUR = 4
  val FIVE = 5

  "Hobbies Repository" should {
    "be able to retrieve all hobbies with their ids" in {
      val result = hobbiesRepositoryModel.result(hobbiesRepositoryModel.repository.retrieveHobbies)
      result mustEqual List(HobbiesModel(ONE, "dancing"), HobbiesModel(TWO, "listening music"),
        HobbiesModel(THREE, "photography"), HobbiesModel(FOUR, "reading novels"), HobbiesModel(FIVE, "watching tv"))
    }
  }

}
