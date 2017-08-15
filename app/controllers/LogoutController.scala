package controllers

import javax.inject.Inject

import models.UserDataRepository
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.Future

class LogoutController @Inject()(userDataRepository: UserDataRepository,
                                 val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def logout: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(adminEmail) =>
        Logger.info("Successfully logged out")
        Future.successful(Redirect(routes.Application.index1())
            .withNewSession.flashing("success" -> "You have logged out!"))

      case None =>
        Logger.error("Error occurred while logout")
        Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}

