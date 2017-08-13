package controllers

import play.api.data.Forms.{mapping, optional, text, _}
import play.api.data._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import scala.util.matching.Regex

case class UserData(firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, mobileNumber: Long, hobbies: List[String], email: String, password: String, confirmPassword: String)

case class UserProfile(firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, mobileNumber: Long, hobbies: List[String], email: String)

case class UserProfileData(firstName: String, middleName: Option[String], lastName: String, age: Int, gender: String, mobileNumber: Long, email: String)

object UserProfileData{
  def apply(list: List[(String, Option[String], String, Int, String, Long, String)]) = {
    val firstName = list.head._1
    val middleName = list.head._2
    val lastName = list.head._3
    val age = list.head._4
    val gender = list.head._5
    val mobileNumber = list.head._6
    val email = list.head._7

    new UserProfileData(firstName, middleName, lastName, age, gender, mobileNumber, email)
  }
}

object UserProfile{
  def apply(userProfileData: UserProfileData, hobbiesSeq: Seq[String]) = {
    val firstName = userProfileData.firstName
    val middleName = userProfileData.middleName
    val lastName = userProfileData.lastName
    val age = userProfileData.age
    val gender = userProfileData.gender
    val mobileNumber = userProfileData.mobileNumber
    val email = userProfileData.email

    val hobbies = hobbiesSeq.toList
    new UserProfile(firstName, middleName, lastName, age, gender, mobileNumber, hobbies, email)
  }
}

case class UserLoginData(email: String, password: String)

case class Hobbies()

class UserForms {

  val userForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "age" -> number(min = 18, max = 75),
      "gender" -> nonEmptyText,
      "mobileNumber" -> longNumber.verifying(mobileNumberCheck()),
      "hobbies" -> list(text).verifying("Select one or more hobbies", hobbies => hobbies.nonEmpty),
      "email" -> email,
      "password" -> text.verifying(passwordCheck()),
      "confirmPassword" -> text.verifying(passwordCheck())
    )(UserData.apply)(UserData.unapply).verifying("Password & confirm password do not match",
      fields => fields match {
        case user => validatePassword(user.password, user.confirmPassword)
      }))

  val userProfileForm = Form(
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "age" -> number(min = 18, max = 75),
      "gender" -> nonEmptyText,
      "mobileNumber" -> longNumber.verifying(mobileNumberCheck()),
      "hobbies" -> list(text),
      "email" -> email
    )(UserProfile.apply)(UserProfile.unapply))

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

  def mobileNumberCheck(errorMessage: String = "error.mobileNumber"): Constraint[Long] = Constraint[Long]("constraint.mobileNumber") {
    mobileNumber =>
      val errors = mobileNumber match {
        case p if p.toString.trim.isEmpty => Seq(ValidationError("Please enter mobile number"))
        case p if p.toString.length != 10 => Seq(ValidationError("Mobile Number is incorrect"))
        case _ => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  }

}
