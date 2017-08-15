package controllers

import javax.inject.Inject

import models.{HobbiesRepository, UserDataRepository, UserPlusHobbiesRepository}
import play.api.Logger
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
        Logger.error("Bad Request " + formWithErrors)
        Future.successful(BadRequest(views.html.login("Play", formWithErrors)))
      },
      loginData => {
        userDataRepository.findByEmail(loginData.email).flatMap {
          case true =>
            Logger.info(s"User with ${loginData.email} exists")
            userDataRepository.validatePassword(loginData.email, loginData.password).flatMap {
              case true =>
                Logger.info("Correct Password")
                userDataRepository.checkIsEnabled(loginData.email).map {
                  case Some(bool) =>
                    if (bool) {
                      Logger.info("User is enabled, redirecting to profile page")
                      Redirect(routes.ProfileController.showUserProfile())
                        .flashing("success" -> "You are successfully logged in!")
                        .withSession("userEmail" -> loginData.email)
                    }
                    else {
                      Logger.info("User is disabled")
                      Redirect(routes.Application.index1())
                        .flashing("unauthorised" -> "You are being disabled")
                    }
                  case None =>
                    Logger.error("No such user found")
                    Redirect(routes.Application.index1())
                    .flashing("unauthorised" -> "Email does not match, register first")
                }
          case false =>
            Logger.error("Password is incorrect")
            Future.successful(Redirect(routes.Application.showLoginPage())
            .flashing("email" -> loginData.email, "error" -> "Incorrect Password"))
        }
          case false =>
            Logger.error("No such user found")
            Future.successful(Redirect(routes.Application.index1())
            .flashing("unauthorised" -> "Email does not match, register first"))
        }
      })
  }

}

