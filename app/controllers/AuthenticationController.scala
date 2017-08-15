package controllers

import javax.inject._
import models.{HobbiesRepository, UserDataModel, UserDataRepository, UserPlusHobbiesRepository}
import org.mindrot.jbcrypt.BCrypt
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import scala.concurrent.Future

@Singleton
class AuthenticationController @Inject()(userDataRepository: UserDataRepository,
                                         forms: UserForms,hobbiesRepository: HobbiesRepository,
                                         userPlusHobbiesRepository: UserPlusHobbiesRepository,
                                         val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def createUserPost: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    forms.userForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.error("Bad request " + formWithErrors)
        hobbiesRepository.retrieveHobbies.map {
          hobbies => Ok(views.html.registration("Play", formWithErrors, hobbies))
        }
      },
      userData => {
        userDataRepository.findByEmail(userData.email).flatMap {
          case true => Logger.info("Email already exists")
            Future.successful(Redirect(routes.Application.showLoginPage())
              .flashing("error" -> "Email already exists, Please Log In"))

          case false =>
            Logger.info("Email is unique, hence trying to add user information in database")
            val hashedPassword = BCrypt.hashpw(userData.password, BCrypt.gensalt())
            val userDataModel = UserDataModel(0, userData.firstName, userData.middleName, userData.lastName, userData.age,
              userData.gender, userData.mobileNumber, userData.email, hashedPassword)

            userDataRepository.store(userDataModel).flatMap {
              case true =>
                Logger.info("User information is stored in user data table")
                userDataRepository.retrieveUserId(userDataModel.email).flatMap{
                      case id: Int if id > 0 =>
                        Logger.info("User Id is retrieved")
                        userPlusHobbiesRepository.addUserHobbies(id, userData.hobbies.map(_.toInt)).map {
                        case true =>
                          Logger.info("Hobbies added")
                          Redirect(routes.ProfileController.showUserProfile())
                          .flashing("success" -> "You are successfully registered!")
                          .withSession("userEmail" -> userDataModel.email)

                        case false =>
                          Logger.error("Hobbies cannot be added")
                          Redirect(routes.Application.index1())
                          .flashing("error" -> "Something went wrong")
                      }
                      case _ =>
                        Logger.error("No such user exists, hence cannot retrieve the ID")
                        Future.successful(Redirect(routes.Application.index1())
                        .flashing("error" -> "No such user exists"))
                    }
              case false =>
                Logger.info("User information cannot be stored in user data table")
                Future.successful(Redirect(routes.Application.index1())
                .flashing("error" -> "Something went wrong"))
            }
        }
      })
  }

}
