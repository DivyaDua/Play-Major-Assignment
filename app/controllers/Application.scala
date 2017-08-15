package controllers

import javax.inject._

import models.HobbiesRepository
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Application @Inject()(val messagesApi: MessagesApi, forms: UserForms,
                            assignmentForm: AssignmentForm,
                            hobbiesRepository: HobbiesRepository)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def index1: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index1())
  }

  def showRegistrationPage: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
     hobbiesRepository.retrieveHobbies.map{
       hobbies => Ok(views.html.registration("Play", forms.userForm, hobbies))}
  }

  def showLoginPage: Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    val email = request.flash.get("email").getOrElse("")
    Ok(views.html.login("Play", forms.userLoginForm.fill(UserLoginData(email, ""))))
  }

  def showForgotPasswordPage: Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.forgotPassword(forms.userForgotPasswordForm))
  }

  def showAddAssignmentPage: Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    Ok(views.html.addAssignment(assignmentForm.assignmentForm))
  }

}
