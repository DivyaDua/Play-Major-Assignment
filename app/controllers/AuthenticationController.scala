package controllers

import javax.inject._

import models.{UserDataModel, UserDataRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import scala.concurrent.Future

case class UserProfile(firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, email: String)

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
          case  Some(_) => Logger.info("Email already exists")
            Future.successful(Redirect(routes.Application.showLoginPage())
              .flashing("information" -> "Email already exists, Please Log In"))

          case None =>

            val userDataModel = UserDataModel(0, userData.firstName, userData.middleName, userData.lastName, userData.age,
            userData.gender, userData.email, userData.password)

            val userProfile = UserProfile(userData.firstName, userData.middleName, userData.lastName, userData.age,
            userData.gender, userData.email)

            userDataRepository.store(userDataModel).map{
              case true => Redirect(routes.Application.display)
                .flashing("success" -> "You are successfully registered!")
                .withSession("userProfile" -> userProfile.email)

              case false => Redirect(routes.Application.display)
                .flashing("error" -> "Something went wrong")
            }
        }
      })
  }

  def createUserLoginPost: Action[AnyContent] = Action{ implicit request: Request[AnyContent] =>
    forms.userLoginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login("Play", formWithErrors))
      },
      userData => {
        Redirect(routes.Application.index())
      })
  }

}
