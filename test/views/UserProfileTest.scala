package views

import controllers.UserForms
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class UserProfileTest extends PlaySpec with MockitoSugar{

  "User Profile" should {
    "be able to display user details in updatable form" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      val isAdmin = true
      when(flash.get("success")) thenReturn None
      when(flash.get("error")) thenReturn None
      val userProfileForm = new UserForms().userProfileForm
      val html = views.html.userProfile.render(userProfileForm, List(), isAdmin, messages, flash)
      assert(html.toString.contains("Profile"))
    }
  }

}
