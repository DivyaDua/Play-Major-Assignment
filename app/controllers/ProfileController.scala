package controllers

import javax.inject._

import models.{HobbiesRepository, UserDataModel, UserDataRepository, UserPlusHobbiesRepository}

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

  lazy val hobbiesList: Future[List[String]] = hobbiesRepository.retrieveHobbies

  def showUserProfile: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => userDataRepository.retrieve(userEmail).flatMap {
        case Nil =>
          Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
          Future.successful(Ok(views.html.index1()))

        case userList: List[UserDataModel] =>
          val user = userList.head
          userPlusHobbiesRepository.getUserHobby(userEmail).flatMap {
            case Nil =>
              Logger.info("Did not receive any hobbies for the user!")
              Future.successful(Ok(views.html.index1()))
            case hobbies: List[String] =>
              Logger.info("Recieved list of hobbies")
              val userProfile = UserProfile(user.firstName, user.middleName, user.lastName, user.age,
                user.gender, user.mobileNumber, hobbies, user.email)

              hobbiesList.map( hobbies =>
                Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile), hobbies))
              )
          }
      }

      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def updateProfile: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(email) =>
        forms.userProfileForm.bindFromRequest.fold(
          formWithErrors => {
            hobbiesRepository.retrieveHobbies.map(
              hobbies =>  BadRequest(views.html.userProfile(formWithErrors, hobbies)))
          },
          userProfile => {
            val userProfileData = UserProfileData(userProfile.firstName, userProfile.middleName,
              userProfile.lastName, userProfile.age, userProfile.gender, userProfile.mobileNumber,
              userProfile.email)

            userDataRepository.updateUserProfile(userProfileData, email).flatMap {
              case true =>
                val hobbiesIdList = hobbiesRepository.retrieveHobbiesID(userProfile.hobbies)
                hobbiesIdList.flatMap(
                  listOfHobbyIds =>
                    userPlusHobbiesRepository.updateUserHobbies(userProfile.email, listOfHobbyIds).map {
                      case true => Redirect(routes.ProfileController.showUserProfile())
                        .flashing("success" -> "Your Profile is updated successfully!")
                        .withSession("userEmail" -> userProfile.email)

                      case false => Redirect(routes.ProfileController.showUserProfile())
                        .flashing("error" -> "Something went wrong, Try to update again")
                    })
              case false => Future.successful(Redirect(routes.ProfileController.showUserProfile())
                .flashing("error" -> "Something went wrong, Try to update again"))
            }})
      case None => Future.successful(Redirect(routes.Application.index1()).flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
