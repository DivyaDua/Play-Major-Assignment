package controllers

import javax.inject.Inject

import models.{UserDataModel, UserDataRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ViewUserController @Inject()(userDataRepository: UserDataRepository,
                                   val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def viewUsers: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(adminEmail) => userDataRepository.retrieveAllUsers.map {
        case Nil =>
          Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
          Redirect(routes.Application.index1()).flashing("error" -> "No Users Found")
        case user: List[UserDataModel] =>
          Logger.info("Displaying All Users with enable & disable buttons")
          Ok(views.html.viewUser(user))
      }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
