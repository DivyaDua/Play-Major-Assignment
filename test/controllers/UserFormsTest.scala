package controllers

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UserFormsTest extends PlaySpec with MockitoSugar {

  val object1 = new UserForms()

  "User Forms Test" should {

     "be able to check if password and confirm password match" in {
       val result = object1.validatePassword("divya", "divya")
       result mustEqual true
     }

  }

}
