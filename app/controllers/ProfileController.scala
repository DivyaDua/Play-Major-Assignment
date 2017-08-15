package controllers

import javax.inject._

import models._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.Future

@Singleton
class ProfileController @Inject()(userDataRepository: UserDataRepository,
                                  forms: UserForms, userPlusHobbiesRepository: UserPlusHobbiesRepository,
                                  hobbiesRepository: HobbiesRepository,
                                  val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def showUserProfile: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => userDataRepository.retrieve(userEmail).flatMap {
        case Nil =>
          Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
          Future.successful(Redirect(routes.Application.index1())
            .flashing("error" -> "No user found, cannot show profile"))

        case userList: List[UserDataModel] =>
          val user = userList.head
          userPlusHobbiesRepository.getUserHobby(user.id).flatMap {
            case Nil =>
              Logger.error("Did not receive any hobbies for the user!")
              Future.successful(Redirect(routes.Application.index1())
                .flashing("error" -> "Hobbies are not retrieved properly"))
            case hobbies: List[Int] =>
              Logger.info("Received list of hobbies")
              val userProfile = UserProfile(user.firstName, user.middleName, user.lastName, user.age,
                user.gender, user.mobileNumber, hobbies)

              hobbiesRepository.retrieveHobbies.map( userHobbies =>
                Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile), userHobbies, user.isAdmin))
              )
          }
      }
      case None =>
        Logger.error("Cannot show profile")
        Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def updateProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    val bool = request.session.get("isAdmin").getOrElse("false").toBoolean
    email match {
      case Some(email) =>
        forms.userProfileForm.bindFromRequest.fold(
          formWithErrors => {
            Logger.error("Bad Request " + formWithErrors)
              hobbiesRepository.retrieveHobbies.map(
                hobbies =>  BadRequest(views.html.userProfile(formWithErrors, hobbies, bool)))
          },
          userProfile => {
            val userProfileData = UserProfileData(userProfile.firstName, userProfile.middleName,
              userProfile.lastName, userProfile.age, userProfile.gender, userProfile.mobileNumber)

            userDataRepository.updateUserProfile(userProfileData, email).flatMap {
              case true => Logger.info("Updated user data table")
                userDataRepository.retrieveUserId(email).flatMap{
                      case id: Int if id > 0 =>
                        Logger.info(s"Retrieved id for user with email $email")
                        userPlusHobbiesRepository.updateUserHobbies(id, userProfile.hobbies).map {
                        case true =>
                          Logger.info("Updated hobbies of user")
                          Redirect(routes.ProfileController.showUserProfile())
                          .flashing("success" -> "Your Profile is updated successfully!")

                        case false =>
                          Logger.error("Failed to update hobbies of user")
                          Redirect(routes.ProfileController.showUserProfile())
                          .flashing("error" -> "Something went wrong, Try to update again")
                      }
                      case _ =>
                        Logger.error(s"No user with email $email exists, hence can't retrieve its id")
                        Future.successful(Redirect(routes.ProfileController.showUserProfile())
                        .flashing("error" -> "Something went wrong, Try to update again"))
                    }
              case false => Logger.error("Failed to update user data table")
                Future.successful(Redirect(routes.ProfileController.showUserProfile())
                .flashing("error" -> "Something went wrong, Try to update again"))
            }})
      case None =>
        Logger.error("User is not in session")
        Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
