package views

import org.scalatestplus.play.PlaySpec

class NavBarTest extends PlaySpec{

  "NavBar" should {
    "add nav bars to all admin pages" in {
      val isAdmin = true
      val activeTab = "profile"
      val html = views.html.navbarFile.render(isAdmin, activeTab)
      assert(html.toString.contains("View Users"))
    }

    "add nav bars to all user pages" in {
      val isAdmin = false
      val activeTab = "profile"
      val html = views.html.navbarFile.render(isAdmin, activeTab)
      assert(!html.toString.contains("View Users"))
    }
  }

}
