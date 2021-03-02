package com.kolotree.command.common

import cats.syntax.eq._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AggregateRootEqualitySpec extends AnyFlatSpec with Matchers {

  case class Person(id: String, firstName: String, lastName: String)
      extends AggregateRoot

  "Two aggregates with same id" should "be equal" in {
    val john = Person("123", "John", "Doe")
    val jane = Person("123", "Jane", "Johnson")

    (john eqv jane) shouldBe true
  }

  "Two aggregates with different id" should "not be equal" in {
    val john = Person("123", "John", "Doe")
    val jane = Person("234", "Jane", "Johnson")

    (john eqv jane) shouldBe false
  }

  "Two aggregates with different id with rest fields the same" should "not be equal" in {
    val thatJohn = Person("123", "John", "Doe")
    val otherJohn = Person("234", "John", "Doe")

    (thatJohn eqv otherJohn) shouldBe false
  }
}
