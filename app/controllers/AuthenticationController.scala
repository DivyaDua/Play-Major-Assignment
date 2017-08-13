package controllers

import javax.inject._

import models.{UserDataModel, UserDataRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import scala.concurrent.Future

@Singleton
class AuthenticationController @Inject()(userDataRepository: UserDataRepository,
                                         forms: UserForms, val messagesApi: MessagesApi)
  extends Controller with I18nSupport{

  implicit val messages: MessagesApi = messagesApi

  def createUserPost: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
    forms.userForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.registration("Play", formWithErrors)))
      },
      userData => {
        userDataRepository.findByEmail(userData.email).flatMap{
          case  true => Logger.info("Email already exists")
            Future.successful(Redirect(routes.Application.showLoginPage())
              .flashing("error" -> "Email already exists, Please Log In"))

          case false =>
            val userDataModel = UserDataModel(0, userData.firstName, userData.middleName, userData.lastName, userData.age,
            userData.gender, userData.mobileNumber, userData.email, userData.password)

            userDataRepository.store(userDataModel).map{
              case true => Redirect(routes.ProfileController.showUserProfile())
                .flashing("success" -> "You are successfully registered!")
                .withSession("userEmail" -> userDataModel.email)

              case false => Redirect(routes.Application.display)
                .flashing("error" -> "Something went wrong")
            }
        }
      })
  }

  def createLoginPost: Action[AnyContent] = Action.async{ implicit request: Request[AnyContent] =>
    val postAction: Seq[String] = request.body.asFormUrlEncoded.get("login")

    val actionLogin = postAction.head
    if (actionLogin.equals("adminLogin")) {
      forms.userLoginForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.login("Play", formWithErrors)))
        },
        userData => {
          Future.successful(Redirect(routes.Application.index1()))
        })
    }
    else {
      forms.userLoginForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.login("Play", formWithErrors)))
        },
        userLoginData => userDataRepository.findByEmail(userLoginData.email).flatMap{
          case true => userDataRepository.validatePassword(userLoginData.password).flatMap{
            case true =>
              userDataRepository.checkIsEnabled(userLoginData.email).map{
                case true => Redirect(routes.ProfileController.showUserProfile())
                  .flashing("success" -> "You are successfully logged in!")
                  .withSession("userEmail" -> userLoginData.email)
                case false => Redirect(routes.Application.index1())
                  .flashing("unauthorised" -> "You are being disabled")
              }
            case false =>  Future.successful(Redirect(routes.Application.showLoginPage())
              .flashing("error" -> "Incorrect Password"))
          }
          case false => Future.successful(Redirect(routes.Application.index1())
            .flashing("unauthorised" -> "Email does not match, Register first"))
        }
      )    }
  }



}
