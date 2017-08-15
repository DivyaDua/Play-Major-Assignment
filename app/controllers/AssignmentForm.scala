package controllers

import play.api.data.Forms._
import play.api.data._

case class Assignment(title: String, description: String)

class AssignmentForm {

  val assignmentForm = Form(
    mapping(
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Assignment.apply)(Assignment.unapply)
  )

}
