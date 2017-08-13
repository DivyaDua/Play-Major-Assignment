package controllers

import javax.inject._

import models.HobbiesRepository
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Application @Inject()(val messagesApi: MessagesApi, forms: UserForms,
                            hobbiesRepository: HobbiesRepository)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

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

  def showRegistrationPage = Action.async{ implicit request: Request[AnyContent] =>
     hobbiesRepository.retrieveHobbies.map{hobbies => Ok(views.html.registration("Play", forms.userForm, hobbies))}
  }

  def showLoginPage = Action{ implicit request: Request[AnyContent] =>
    val email = request.flash.get("email").getOrElse("")
    Ok(views.html.login("Play", forms.userLoginForm.fill(UserLoginData(email, ""))))
  }

  def showForgotPasswordPage = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.forgotPassword(forms.userForgotPasswordForm))
  }

  /*def showProfile(userProfile: UserProfile) = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile)))
  }
*/

}
