package views

import models.AssignmentModel
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ShowAssignmentToAdmin extends PlaySpec with MockitoSugar{

  "Show Assignment To Admin" should {
    "be able to show assignments to admin with delete button" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      when(flash.get("status")) thenReturn None
      val html = views.html.showAssignmentToAdmin.render(List(AssignmentModel(1, "Play", "Create user login application")), messages, flash)
      assert(html.toString.contains("Delete"))
    }

    "not be able to show any assignments if assignments list is empty" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      when(flash.get("status")) thenReturn None
      val html = views.html.showAssignmentToAdmin.render(List(), messages, flash)
      assert(html.toString.contains("No Assignments found"))
    }
  }

}
