package views

import models.AssignmentModel
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class ShowAssignmentToUser extends PlaySpec with MockitoSugar{

  "Show Assignment To User" should {
    "be able to show assignments to users without delete button" in {
      val messages = mock[Messages]
      val html = views.html.showAssignmentToUser.render(List(), messages)
      assert(html.toString.contains("No Assignments found"))
      assert(!html.toString.contains("Delete"))
    }

    "be able to show assignments to admin with delete button" in {
      val messages = mock[Messages]
      val html = views.html.showAssignmentToUser.render(List(AssignmentModel(1, "Play", "Create user login application")), messages)
      assert(html.toString.contains("Title"))
    }
  }

}
