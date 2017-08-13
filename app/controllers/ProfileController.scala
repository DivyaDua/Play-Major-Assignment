package controllers

import javax.inject._

import models.{UserDataModel, UserDataRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import scala.concurrent.Future

@Singleton
class ProfileController @Inject()(userDataRepository: UserDataRepository,
                                  forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  def showUserProfile = Action.async{ implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => userDataRepository.retrieve(userEmail).map{
          userProfile => Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile)))
        }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def updateProfile = Action.async { implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(email) =>
        forms.userProfileForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.userProfile(formWithErrors)))
        },
        userProfileData => {

          userDataRepository.updateUserProfile(userProfileData, email).map{
            case true => Redirect(routes.ProfileController.showUserProfile())
              .flashing("success" -> "Your Profile is updated successfully!")
              .withSession("userEmail" -> userProfileData.email)

            case false => Redirect(routes.Application.display)
              .flashing("error" -> "Something went wrong")
          }
        })
      case None => Future.successful(Redirect(routes.Application.index1()).flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
