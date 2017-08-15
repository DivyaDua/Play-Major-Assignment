package controllers

import javax.inject.Inject

import models.{AssignmentModel, AssignmentRepository, UserDataRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HandleAssignmentController @Inject()(userDataRepository: UserDataRepository,
                                           assignmentRepository: AssignmentRepository,
                                           form: AssignmentForm,
                                           val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  implicit val messages: MessagesApi = messagesApi

  def deleteAssignment(id: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(email) =>
        assignmentRepository.delete(id).map{
        case true =>
          Logger.info("Deleting Assignment")
          Redirect(routes.AssignmentController.viewAssignment())
          .flashing("status" -> "Assignment Deleted!")

        case false => Redirect(routes.AssignmentController.viewAssignment())
          .flashing("status" -> "Something went wrong!")
      }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def addAssignment: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => form.assignmentForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.addAssignment(formWithErrors)))
        },
        assignmentData => {
          val assignmentModel = AssignmentModel(0, assignmentData.title, assignmentData.description)
          Logger.info("Adding Assignment")
          assignmentRepository.addAssignment(assignmentModel).map {
            case true =>
              Logger.info("Added Assignment to the database")
              Redirect(routes.AssignmentController.viewAssignment())
                .flashing("status" -> "Assignment added!")

            case false =>
              Logger.info("Failed to add Assignment to the database")
              Redirect(routes.AssignmentController.viewAssignment())
                .flashing("status" -> "Failed to add assignment!")
          }
        })
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
