import com.softwaremill.macwire.wire
import controllers.PetController
import daos.PetDao
import io.getquill.SnakeCase
import me.waynewang12.quill.{MysqlQuillSource, QuillMySqlComponents}
import play.api.db.HikariCPComponents
import play.api.db.evolutions.EvolutionsComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.routing.sird._
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext}

class AppLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application =
    new BuiltInComponentsFromContext(context) with QuillMySqlComponents[SnakeCase] with HikariCPComponents with EvolutionsComponents {

      applicationEvolutions

      override lazy val naming: SnakeCase                    = SnakeCase
      lazy val quillMysqlSource: MysqlQuillSource[SnakeCase] = quillSources.source("default")
      lazy val petDao: PetDao                                = wire[PetDao]
      lazy val petController: PetController                  = wire[PetController]

      override def router: Router =
        Router.from {
          case POST(p"/pets")        =>
            petController.addPet()
          case GET(p"/pets")         =>
            petController.getPets
          case GET(p"/pets/${name}") =>
            petController.getPet(name)
        }

      override def httpFilters: Seq[EssentialFilter] = Seq.empty

    }.application
}
