package dev.fsvehla.scala3Derivation
package derivation

import scala.deriving.*
import scala.compiletime.*

object DeriveJsonDecoder:
  inline def apply[T](using m: Mirror.Of[T]): JsonEncoder[T] =
    lazy val elemInstances = summonAll[m.MirroredElemTypes]

    inline m match
      case p: Mirror.ProductOf[T] =>
        deriveProduct(p, elemInstances)

      case s: Mirror.SumOf[T] =>
        deriveSum(s, elemInstances)

  private inline def deriveProduct[T](p: Mirror.ProductOf[T], elems: List[JsonEncoder[_]]): JsonEncoder[T] = {
    val params           = getParams[T, p.MirroredElemLabels, p.MirroredElemTypes]
    val customFieldNames = CaseParameterAnnotations[T, jsonField]
    val names            = elemLabels[p.MirroredElemLabels]

    def encode0(encoder: JsonEncoder[_], value: Any) =
      encoder.asInstanceOf[JsonEncoder[Any]](value)

    new JsonEncoder[T]:
      def apply(x: T): Json = {
        val values: Iterator[_] = x.asInstanceOf[Product].productIterator

        val encoded = values
          .zip(elems)
          .zipWithIndex
          .toList
          .map { case ((value, encoder), index) =>
            names(index) -> encode0(encoder, value)
           }

          Json.Obj(encoded)
      }
    }

  private inline def summonAll[T <: Tuple]: List[JsonEncoder[_]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts)  =>
        summonInline[JsonEncoder[t]] :: summonAll[ts]

  private inline def getParams[T, Labels <: Tuple, Params <: Tuple]: List[String] =
    inline erasedValue[(Labels, Params)] match
      case _: (EmptyTuple, EmptyTuple)     => Nil
      case _: ((l *: ltail), (p *: ptail)) =>
        constValue[l].asInstanceOf[String] :: getParams[T, ltail, ptail]

  private inline def elemLabels[T <: Tuple]: List[String] =
    inline erasedValue[T] match {
      case _: EmptyTuple => Nil
      case _: (t *: ts)  =>
        constValue[t].asInstanceOf[String] :: elemLabels[ts]
    }

  private inline def deriveSum[T](s: Mirror.SumOf[T], elems: List[JsonEncoder[_]]): JsonEncoder[T] =
    new JsonEncoder[T]:
      def apply(x: T): Json = Json.Null
