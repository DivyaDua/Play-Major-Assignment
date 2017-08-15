package views

import controllers.UserForms
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages

class RegistrationTest extends PlaySpec with MockitoSugar{

  "Registration" should {
    "be able to render page with registration form" in {
      val messages = mock[Messages]
      val userForm = new UserForms().userForm
      val html = views.html.registration.render("Play", userForm, List(), messages)
      assert(html.toString.contains("Sign Up Form"))
    }
  }

}
