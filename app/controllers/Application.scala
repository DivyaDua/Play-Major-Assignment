package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class Application @Inject()(implicit val messagesApi: MessagesApi, userForms: UserForms)
  extends Controller with I18nSupport{

  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def display = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.display("Play Error"))
  }

  def showRegistrationForm = Action{ implicit request: Request[AnyContent] =>
     Ok(views.html.registration("Play", userForms.userForm))
  }

  def showLoginPage = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.login("Play", userForms.userLoginForm))
  }

}
