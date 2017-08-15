package views

import controllers.AssignmentForm
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class AddAssignmentTest extends PlaySpec with MockitoSugar{

  "Add Assignment" should {
    "be able to render page for adding assignment" in {
      val messages = mock[Messages]
      val flash = mock[Flash]
      val assignmentForm = new AssignmentForm().assignmentForm

      when(flash.get("error")) thenReturn None
      when(flash.get("success")) thenReturn None

      val html = views.html.addAssignment.render(assignmentForm, messages, flash)
      assert(html.toString.contains("Assignment Details"))

    }
  }

}
