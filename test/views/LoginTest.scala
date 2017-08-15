package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class LoginTest extends PlaySpec with MockitoSugar{

  "Login" should {
    "display login page to the users" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      when(flash.get("error")) thenReturn None
      when(flash.get("success")) thenReturn None
      val userLoginForm = new UserForms().userLoginForm
      val html = views.html.login.render("Play", userLoginForm,messages, flash)
      assert(html.toString.contains("Log In"))
    }
  }

}
