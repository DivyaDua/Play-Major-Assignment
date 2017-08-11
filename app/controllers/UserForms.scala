package controllers

import play.api.data.Forms.{mapping, optional, text, _}
import play.api.data._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.matching.Regex

case class UserData(firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, email: String, password: String, confirmPassword: String)

case class UserLoginData(email: String, password: String)

class UserForms {

  val userForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "age" -> number(min = 18, max = 75),
      "gender" -> nonEmptyText,
      "email" -> email,
      "password" -> text.verifying(passwordCheck()),
      "confirmPassword" -> text.verifying(passwordCheck())
    )(UserData.apply)(UserData.unapply).verifying("Password & confirm password do not match",
      fields => fields match {
        case user => validatePassword(user.password, user.confirmPassword)
      }))

  val userLoginForm = Form(
    mapping(
      "email" -> email,
      "password" -> text.verifying(passwordCheck())
    )(UserLoginData.apply)(UserLoginData.unapply)
  )
  val allNumbers: Regex = """\d*""".r
  val allLetters: Regex = """[A-Za-z]*""".r

  def validatePassword(password: String, confirmPassword: String): Boolean = password == confirmPassword

  def passwordCheck(errorMessage: String = "error.password"): Constraint[String] = Constraint[String]("constraint.password") {
    password =>
      val errors = password match {
        case p if p.trim.isEmpty => Seq(ValidationError("Please enter password"))
        case p if p.length < 8 => Seq(ValidationError("Password is too short"))
        case allNumbers() => Seq(ValidationError("Password is all numbers"))
        case allLetters() => Seq(ValidationError("Password is all letters"))
        case _ => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  }

}
