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
  lazy val hobbiesList: Future[List[HobbiesModel]] = hobbiesRepository.retrieveHobbies

  def showUserProfile: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    email match {
      case Some(userEmail) => userDataRepository.retrieve(userEmail).flatMap {
        case Nil =>
          Logger.info("Did not receive any user with given UserID! Redirecting to welcome page!")
          Future.successful(Ok(views.html.index1()))

        case userList: List[UserDataModel] =>

          val user = userList.head
          userPlusHobbiesRepository.getUserHobby(user.id).flatMap {
            case Nil =>
              Logger.info("Did not receive any hobbies for the user!")
              Future.successful(Ok(views.html.index1()))
            case hobbies: List[Int] =>
              Logger.info("Received list of hobbies")
              val userProfile = UserProfile(user.firstName, user.middleName, user.lastName, user.age,
                user.gender, user.mobileNumber, hobbies)

              hobbiesList.map( userHobbies =>
                Ok(views.html.userProfile(forms.userProfileForm.fill(userProfile), userHobbies, user.isAdmin))
              )
          }
      }
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

  def updateProfile: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val email = request.session.get("userEmail")
    val bool = request.session.get("isAdmin").getOrElse("false").toBoolean
    email match {
      case Some(email) =>
        forms.userProfileForm.bindFromRequest.fold(
          formWithErrors => {
              hobbiesRepository.retrieveHobbies.map(
                hobbies =>  BadRequest(views.html.userProfile(formWithErrors, hobbies, bool)))
          },
          userProfile => {
            val userId = userDataRepository.retrieveUserId(email)
            val userProfileData = UserProfileData(userProfile.firstName, userProfile.middleName,
              userProfile.lastName, userProfile.age, userProfile.gender, userProfile.mobileNumber)

            userDataRepository.updateUserProfile(userProfileData, email).flatMap {
              case true =>
                userId.flatMap{
                      case id: Int if id > 0 => userPlusHobbiesRepository.updateUserHobbies(id, userProfile.hobbies).map {
                        case true => Redirect(routes.ProfileController.showUserProfile())
                          .flashing("success" -> "Your Profile is updated successfully!")

                        case false => Redirect(routes.ProfileController.showUserProfile())
                          .flashing("error" -> "Something went wrong, Try to update again")
                      }
                      case 0 => Future.successful(Redirect(routes.ProfileController.showUserProfile())
                        .flashing("error" -> "Something went wrong, Try to update again"))
                    }
              case false => Future.successful(Redirect(routes.ProfileController.showUserProfile())
                .flashing("error" -> "Something went wrong, Try to update again"))
            }})
      case None => Future.successful(Redirect(routes.Application.index1())
        .flashing("unauthorised" -> "You need to log in first!"))
    }
  }

}
