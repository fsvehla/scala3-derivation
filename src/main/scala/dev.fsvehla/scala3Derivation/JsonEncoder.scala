package dev.fsvehla.scala3Derivation

import scala.deriving.*

trait JsonEncoder[A]:
  self =>
    def apply(x: A): Json

    def contraMap[B](f: B => A): JsonEncoder[B] =
      new JsonEncoder[B]:
        def apply(x: B) =
          self(f(x))

object JsonEncoder:
  given JsonEncoder[Int] with
    def apply(x: Int) =
      Json.Num(x)

  given JsonEncoder[String] with
    def apply(x: String) =
      Json.Str(x)

  given listEncoder[A](using A: JsonEncoder[A]): JsonEncoder[List[A]] with
    def apply(xs: List[A]) =
      Json.Arr(xs.map(x => A(x)))

  given mapEncoder[V](using V: JsonEncoder[V]): JsonEncoder[Map[String, V]] with
    def apply(map: Map[String, V]) =
      Json.Obj(map.toList.map((k, v) => (k -> V(v))))

  inline given derived[T](using m: Mirror.Of[T]): JsonEncoder[T] =
    derivation.DeriveJsonDecoder[T]

  object syntax:
    extension [T: JsonEncoder](t: T)
      def toJson: String = summon[JsonEncoder[T]](t).render
