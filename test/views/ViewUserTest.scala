package views

import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ViewUserTest extends PlaySpec with MockitoSugar{

  "View User" should {
    "be able to show all users" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      when(flash.get("status")) thenReturn None
      val html = views.html.viewUser.render(List(), messages, flash)
      assert(html.toString.contains("User Details"))
    }
  }

}
