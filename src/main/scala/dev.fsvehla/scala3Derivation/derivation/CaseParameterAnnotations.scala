package dev.fsvehla.scala3Derivation.derivation

import scala.quoted._

object CaseParameterAnnotations:
  inline def apply[T, A]: List[(Int, A)] = ${ applyImpl[T, A] }

  private def applyImpl[T, A](using qctx: Quotes, tpe: Type[T], tpeA: Type[A]): Expr[List[(Int, A)]] =
    import qctx.reflect._

    val T = TypeRepr.of[T]
    val A = TypeRepr.of[A]

    /**
     * For case classes annotations are not attached to .typeSymbol.caseFields on the TypeRepr,
     * but to the primary constructor’s first parameter list
     */
    val caseClassFieldSymbols: List[Symbol] =
      T.
        classSymbol
        .get
        .primaryConstructor
        .paramSymss(0)

    val matchingAnnotations: List[(Int, Term)] =
      caseClassFieldSymbols
        .zipWithIndex
        .flatMap { (symbol, index) =>
          symbol
            .annotations
            .filter(_.tpe <:< A)
            .map(s => index -> s)
        }

    val exprs: List[Expr[(Int, A)]] =
      matchingAnnotations
      .map { (index, term) =>
        val asA = term.asExpr.asInstanceOf[Expr[A]]

        '{ ${ Expr(index) } -> ${ asA } }
      }

    Expr.ofList(exprs)
