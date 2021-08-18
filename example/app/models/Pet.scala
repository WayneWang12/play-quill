package models

import play.api.libs.json.{Format, Json}

case class Pet(name: String, age: Int, color:String)

object Pet {
  implicit val format:Format[Pet] = Json.format
}
