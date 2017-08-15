package controllers

import javax.inject.Inject

import models.UserDataRepository
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnableDisableController @Inject()(userDataRepository: UserDataRepository,
                                        val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def enableUser(userName: String, userEmailValue: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) =>
              userDataRepository.enableUser(userEmailValue).map{
              case true => Redirect(routes.ViewUserController.viewUsers())
                .flashing("status" -> s"User $userName is successfully enabled!")

              case false => Redirect(routes.ViewUserController.viewUsers())
                .flashing("status" -> "Something went wrong!")
            }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def disableUser(userName: String, userEmailValue: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) =>
            userDataRepository.disableUser(userEmailValue).map{
            case true =>
              Redirect(routes.ViewUserController.viewUsers())
              .flashing("status" -> s"User $userName is successfully disabled!")

            case false => Redirect(routes.ViewUserController.viewUsers())
              .flashing("status" -> "Something went wrong!")
          }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
