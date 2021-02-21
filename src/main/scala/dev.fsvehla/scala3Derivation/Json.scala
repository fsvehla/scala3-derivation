package dev.fsvehla.scala3Derivation

import java.{ lang => j }
import JsonEncoder.*
import scala.annotation

case class jsonField(name: String) extends annotation.Annotation

enum Json:
  case Null
  case Str(value: String)
  case Num(value: BigDecimal)
  case Arr(values: List[Json])
  case Obj(pairs: List[(String, Json)])

  def render: String = this match
    case Null    => "null"
    case Str(v)  => s""""$v""""
    case Num(d)  => f"$d"
    case Arr(xs) => xs.map(_.render).mkString("[", ", ", "]")
    case Obj(xs) => "{ " ++ xs.map { case (k, v) =>  '"'.toString ++ k ++ '"'.toString ++ s": ${ v.render }" }.mkString(", ") ++ " }"

    override def toString =
      render

object Json:
  def encode[A](x: A)(using encoder: JsonEncoder[A]): Json =
    encoder(x)

  def render[A](x: A)(using encoder: JsonEncoder[A]): j.String =
    encode(x).render
