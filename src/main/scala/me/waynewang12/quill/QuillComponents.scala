package me.waynewang12.quill

import io.getquill._
import play.api.db.{ DBApi, DBComponents }

class QuillMysqlSourceApi[Naming <: NamingStrategy](
    dbApi: DBApi,
    naming: Naming
) {
  private lazy val sourceMaps = dbApi
    .databases()
    .map { db =>
      db.name -> new MysqlQuillSource[Naming](db, naming)
    }
    .toMap

  def source(name: String): MysqlQuillSource[Naming] = sourceMaps(name)
}

trait QuillMySqlComponents[Naming <: NamingStrategy] extends DBComponents {
  def naming: Naming
  lazy val quillSources = new QuillMysqlSourceApi[Naming](dbApi, naming)
}
