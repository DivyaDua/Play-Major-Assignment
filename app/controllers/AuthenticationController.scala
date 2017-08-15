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
          hobbies => BadRequest(views.html.registration("Play", formWithErrors, hobbies))
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
                val hobbiesIdList = hobbiesRepository.retrieveHobbiesID(userData.hobbies)
                hobbiesIdList.flatMap(
                  listOfHobbyIds =>
                    userDataRepository.retrieveUserId(userDataModel.email).flatMap{
                      case id: Int if id > 0 =>
                        Logger.info("User Id is retrieved")
                        userPlusHobbiesRepository.addUserHobbies(id, listOfHobbyIds).map {
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
                      case id: Int if id == 0 =>
                        Logger.error("No such user exists, hence cannot retrieve the ID")
                        Future.successful(Redirect(routes.Application.index1())
                        .flashing("error" -> "No such user exists"))
                    })
              case false =>
                Logger.info("User information cannot be stored in user data table")
                Future.successful(Redirect(routes.Application.index1())
                .flashing("error" -> "Something went wrong"))
            }
        }
      })
  }

  def createLoginPost: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
      forms.userLoginForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.login("Play", formWithErrors)))
        },
        loginData => userDataRepository.findByEmail(loginData.email).flatMap{
          case true =>
            userDataRepository.validatePassword(loginData.email, loginData.password).flatMap{
              case true =>
                userDataRepository.checkIsAdmin(loginData.email).flatMap{
                  case Some(bool) if bool=> Future.successful(Redirect(routes.ProfileController.showUserProfile())
                    .flashing("success" -> "You are successfully logged in!")
                    .withSession("userEmail" -> loginData.email))

                  case Some(bool) if !bool =>
                    userDataRepository.checkIsEnabled(loginData.email).map{
                    case Some(value) if value => Redirect(routes.ProfileController.showUserProfile())
                      .flashing("success" -> "You are successfully logged in!")
                      .withSession("userEmail" -> loginData.email)

                    case Some(value) if !value => Redirect(routes.Application.index1())
                      .flashing("unauthorised" -> "You are being disabled")

                    case None => Redirect(routes.Application.index1())
                      .flashing("unauthorised" -> "Email does not match, register first")
                  }
                  case None => Future.successful(Redirect(routes.Application.index1())
                    .flashing("unauthorised" -> "Email does not match, register first"))
                }
              case false =>  Future.successful(Redirect(routes.Application.showLoginPage())
                .flashing("email" -> loginData.email,"error" -> "Incorrect Password"))
            }
          case false => Future.successful(Redirect(routes.Application.index1())
            .flashing("unauthorised" -> "Email does not match, register first"))
        }
      )
    }

}
