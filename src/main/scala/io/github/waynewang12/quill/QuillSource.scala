package io.github.waynewang12.quill

import io.getquill._
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import play.api.db.Database
import zio.{ Has, Runtime, ZIO }

import java.io.Closeable
import javax.sql.DataSource
import scala.concurrent.Future
import scala.language.experimental.macros

abstract class QuillSource[Dialect <: SqlIdiom, Naming <: NamingStrategy](
    db: Database
) {
  val ctx: ZioJdbcContext[Dialect, Naming]
  import ctx._
  import io.getquill.context.ZioJdbc.QuillZioExt
  implicit val env: Implicit[Has[DataSource with Closeable]]        = Implicit(
    Has(db.dataSource.asInstanceOf[DataSource with Closeable])
  )
  def runZio[T](zio: ZIO[ctx.Environment, Throwable, T]): Future[T] =
    Runtime.default.unsafeRunToFuture(zio.implicitDS)

  def run[T](quoted: Quoted[T]): Future[T] = macro RunMacro.run[T]
  def run[T](quoted: Quoted[Query[T]]): Future[List[T]] = macro RunMacro.run[T]

  def run(quoted: Quoted[Action[_]]): Future[Long] = macro RunMacro.runSingle
  def run[T](quoted: Quoted[ActionReturning[_, T]]): Future[T] =
    macro RunMacro.run[T]
  def runBatchAction(
      quoted: Quoted[BatchAction[Action[_]]]
  ): Future[List[Long]] = macro RunMacro.runSingle
  def run[T](
      quoted: Quoted[BatchAction[ActionReturning[_, T]]]
  ): Future[List[T]] = macro RunMacro.run[T]

}

class MysqlQuillSource[Naming <: NamingStrategy](db: Database, naming: Naming)
    extends QuillSource[MySQLDialect, Naming](db) {
  override val ctx: ZioJdbcContext[MySQLDialect, Naming] =
    new MysqlZioJdbcContext[Naming](naming)
}
