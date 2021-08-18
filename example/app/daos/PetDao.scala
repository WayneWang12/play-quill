package daos

import io.getquill.SnakeCase
import io.github.waynewang12.quill.MysqlQuillSource
import models.Pet

import scala.concurrent.{ExecutionContext, Future}

class PetDao(mysqlQuillSource: MysqlQuillSource[SnakeCase])(implicit ec: ExecutionContext) {
  import mysqlQuillSource.ctx._

  private val pets = quote(query[Pet])

  def addPet(pet: Pet): Future[Pet] =
    mysqlQuillSource.run(pets.insert(lift(pet))).map { count =>
      if (count > 0) pet
      else throw new Exception("add pet failed!")
    }

  def getPet(name: String): Future[Option[Pet]] =
    mysqlQuillSource.run(pets.filter(_.name == lift(name))).map(_.headOption)

  def getPets: Future[List[Pet]] = mysqlQuillSource.run(pets)

}
