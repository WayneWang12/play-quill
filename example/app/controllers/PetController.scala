package controllers

import daos.PetDao
import models.Pet
import play.api.libs.json.Json
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents }

import scala.concurrent.ExecutionContext

class PetController(cc: ControllerComponents, petDao: PetDao)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {
  def addPet(): Action[Pet] =
    Action.async(parse.json[Pet]) { r =>
      petDao.addPet(r.body).map(pet => Ok(Json.toJson(pet)))
    }

  def getPet(name: String): Action[AnyContent] =
    Action.async {
      petDao.getPet(name).map {
        case Some(pet) =>
          Ok(Json.toJson(pet))
        case None      =>
          NotFound(s"pet with name $name is not found!")
      }
    }

  def getPets: Action[AnyContent] =
    Action.async {
      petDao.getPets.map(pets => Ok(Json.toJson(pets)))
    }
}
