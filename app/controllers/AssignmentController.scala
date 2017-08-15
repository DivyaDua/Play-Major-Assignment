package controllers

import javax.inject.Inject

import models.{AssignmentModel, AssignmentRepository, UserDataRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AssignmentController @Inject()(userDataRepository: UserDataRepository,
                                     assignmentRepository: AssignmentRepository,
                                     val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def viewAssignment: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => assignmentRepository.retrieveAssignments.flatMap {
        case Nil =>
          Logger.info("Did not receive any user Assignment! Redirecting to welcome page!")
          Future.successful(Ok(views.html.index1()))
        case assignmentList: List[AssignmentModel] =>
          userDataRepository.checkIsAdmin(userEmail).map{
            case Some(bool) if bool =>
              Logger.info("Showing assignment with delete option to the admin")
              Ok(views.html.showAssignmentToAdmin(assignmentList))
            case Some(bool) if !bool =>
              Logger.info("Showing assignment to the normal user")
              Ok(views.html.showAssignmentToUser(assignmentList))
            case None =>
              Logger.info("Not a valid user, cannot show the details")
              Redirect(routes.Application.index1())
                .flashing("unauthorised" -> "Email does not match, register first!")
          }
      }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}


