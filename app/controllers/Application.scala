package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class Application @Inject()(implicit val messagesApi: MessagesApi, forms: UserForms)
  extends Controller with I18nSupport{

  def index1 = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index1())
  }

  def showOptionUserOrAdmin = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.userOrAdmin())
  }

  def showAdminLoginPage = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.login("Play", forms.userLoginForm))
  }

  def display = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.display("Play"))
  }

  def showRegistrationPage = Action{ implicit request: Request[AnyContent] =>
     Ok(views.html.registration("Play", forms.userForm))
  }

  def showLoginPage = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.login("Play", forms.userLoginForm))
  }

  /*def showProfile(userProfile: UserProfile) = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile)))
  }
*/

}
