package io.github.waynewang12.quill

import io.getquill._
import io.getquill.context.ContextEffect
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import io.getquill.context.qzio.ZioJdbcContext
import io.getquill.context.sql.idiom.SqlIdiom
import play.api.db.Database
import zio.{ Has, Runtime, ZIO }

import java.io.Closeable
import java.sql.{ Connection, SQLException }
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

  def runTransaction[T](zio: ZIO[ctx.Environment, Throwable, T]): Future[T] = runZio(ctx.transaction(zio))

  def toZio[T](quoted: Quoted[T]): ZIO[ctx.Environment, Throwable, T] = macro RunMacro.toZio[T]
  def toZio[T](quoted: Quoted[Query[T]]): ZIO[ctx.Environment, Throwable, List[T]] = macro RunMacro.toZio[T]
  def toZio(quoted: Quoted[Action[_]]): ZIO[ctx.Environment, Throwable, Long] = macro RunMacro.toZioSingle
  def toZio[T](quoted: Quoted[ActionReturning[_, T]]): ZIO[ctx.Environment, Throwable, T] = macro RunMacro.toZio[T]
  def toZioBatchAction(quoted: Quoted[BatchAction[Action[_]]]): ZIO[ctx.Environment, Throwable, List[Long]] =
    macro RunMacro.toZioSingle
  def toZio[T](quoted: Quoted[BatchAction[ActionReturning[_, T]]]): ZIO[ctx.Environment, Throwable, List[T]] =
    macro RunMacro.run[T]

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
    new MysqlZioJdbcContext[Naming](naming) {
      override protected val effect = new ContextEffect[Result] {
        override def wrap[T](t: => T): ZIO[Has[Connection], SQLException, T] =
          throw new IllegalArgumentException("Runner not used for zio context.")
        override def push[A, B](
            result: ZIO[Has[Connection], SQLException, A]
        )(f: A => B): ZIO[Has[Connection], SQLException, B]                  =
          result.map(f)
        override def seq[A](
            f: List[ZIO[Has[Connection], SQLException, A]]
        ): ZIO[Has[Connection], SQLException, List[A]]                       =
          ZIO.collectAll(f)
      }
    }
}
