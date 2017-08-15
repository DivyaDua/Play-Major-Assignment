package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ForgotPasswordTest extends PlaySpec with MockitoSugar{

  "Forgot Password" should {
    "be able to display form to update password" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      val userForgotPasswordForm = new UserForms().userForgotPasswordForm
      when(flash.get("error")) thenReturn None
      val html = views.html.forgotPassword.render(userForgotPasswordForm, messages, flash)
      assert(html.toString.contains("Update Password"))
    }

  }

}
