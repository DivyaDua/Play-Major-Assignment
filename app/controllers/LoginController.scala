package controllers

import javax.inject.Inject

import models.{HobbiesRepository, UserDataRepository, UserPlusHobbiesRepository}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(userDataRepository: UserDataRepository,
                                forms: UserForms,hobbiesRepository: HobbiesRepository,
                                userPlusHobbiesRepository: UserPlusHobbiesRepository,
                                val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def createLoginPost: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
    forms.userLoginForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.login("Play", formWithErrors)))
      },
      loginData => userDataRepository.findByEmail(loginData.email).flatMap{
        case true =>
          userDataRepository.validatePassword(loginData.email, loginData.password).flatMap{
            case true =>
              userDataRepository.checkIsAdmin(loginData.email).flatMap{
                case Some(bool) if bool=> Future.successful(Redirect(routes.ProfileController.showUserProfile())
                  .flashing("success" -> "You are successfully logged in!")
                  .withSession("userEmail" -> loginData.email))

                case Some(bool) if !bool =>
                  userDataRepository.checkIsEnabled(loginData.email).map{
                    case Some(value) if value => Redirect(routes.ProfileController.showUserProfile())
                      .flashing("success" -> "You are successfully logged in!")
                      .withSession("userEmail" -> loginData.email)

                    case Some(value) if !value => Redirect(routes.Application.index1())
                      .flashing("unauthorised" -> "You are being disabled")

                    case None => Redirect(routes.Application.index1())
                      .flashing("unauthorised" -> "Email does not match, register first")
                  }
                case None => Future.successful(Redirect(routes.Application.index1())
                  .flashing("unauthorised" -> "Email does not match, register first"))
              }
            case false =>  Future.successful(Redirect(routes.Application.showLoginPage())
              .flashing("email" -> loginData.email,"error" -> "Incorrect Password"))
          }
        case false => Future.successful(Redirect(routes.Application.index1())
          .flashing("unauthorised" -> "Email does not match, register first"))
      }
    )
  }

}
