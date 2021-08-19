package io.github.waynewang12.quill

import scala.reflect.macros.whitebox.{ Context => MacroContext }

private[quill] class RunMacro(val c: MacroContext) {

  import c.universe._

  def run[T](quoted: Tree)(implicit t: WeakTypeTag[T]): Tree =
    q"""
      import ${c.prefix}._
      runZio(ctx.run($quoted))
    """

  def runSingle(quoted: Tree): Tree = q"""
      import ${c.prefix}._
      runZio(ctx.run($quoted))
    """

  def toZio[T](quoted: Tree)(implicit t: WeakTypeTag[T]): Tree =
    q"""
      import ${c.prefix}._
      ctx.run($quoted)
    """

  def toZioSingle(quoted: Tree): Tree = q"""
      import ${c.prefix}._
      ctx.run($quoted)
    """
}
