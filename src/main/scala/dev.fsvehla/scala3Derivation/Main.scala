package dev.fsvehla.scala3Derivation

import JsonEncoder.syntax.*

case class User(id: Int, name: String) derives JsonEncoder
case class User0(id: Int, @jsonField("user_name") name: String) derives JsonEncoder

@main def derivationMain() =
  println(Map("a" -> 1, "b" -> 2).toJson)
  println(User(1001, "Bob").toJson)
  println(User0(1002, "Jill").toJson)
