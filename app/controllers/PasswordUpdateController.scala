package controllers

import javax.inject.Inject

import models.{HobbiesRepository, UserDataRepository, UserPlusHobbiesRepository}
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PasswordUpdateController @Inject()(userDataRepository: UserDataRepository,
                                         forms: UserForms,hobbiesRepository: HobbiesRepository,
                                         userPlusHobbiesRepository: UserPlusHobbiesRepository,
                                         val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def updatePassword: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    forms.userForgotPasswordForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Bad request " + formWithErrors)
          Future.successful(BadRequest(views.html.forgotPassword(formWithErrors)))
        },
      userForgotPasswordData => {
        userDataRepository.findByEmail(userForgotPasswordData.email).flatMap{
          case true =>
            val hashedPassword = BCrypt.hashpw(userForgotPasswordData.newPassword, BCrypt.gensalt())
            Logger.info("Updating Password")

            userDataRepository.updatePassword(userForgotPasswordData.email, hashedPassword).map{
              case true => Redirect(routes.Application.showLoginPage())
                .flashing("" -> "Password Updated, You can now login")

              case false => Logger.error("Failed to update password")
                Redirect(routes.Application.showForgotPasswordPage())
                  .flashing("error" -> "Something went wrong, try again")
            }
          case false =>
            Logger.error("Email does not match while trying to update password")
            Future.successful(Redirect(routes.Application.index1())
              .flashing("unauthorised" -> "You are not a registered user, Please Register!"))

        }
      })
  }

}
