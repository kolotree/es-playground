package com.kolotree.command.common

import cats.syntax.eq._
import com.kolotree.common.eventing.Event
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import TestValues._

class AggregateEqualitySpec extends AnyFlatSpec with Matchers {
  "Two aggregates with same id" should "be equal" in {
    val john = Person("123", "John", "Doe", List.empty[Event], -1)
    val jane = Person("123", "Jane", "Johnson", List.empty[Event], -1)

    (john eqv jane) shouldBe true
  }

  "Two aggregates with different id" should "not be equal" in {
    val john = Person("123", "John", "Doe", List.empty[Event], -1)
    val jane = Person("234", "Jane", "Johnson", List.empty[Event], -1)

    (john eqv jane) shouldBe false
  }

  "Only ids" should "be considered when comparing aggregates" in {
    val thatJohn = Person("123", "John", "Doe", List.empty[Event], -1)
    val otherJohn = Person("234", "John", "Doe", List.empty[Event], -1)

    (thatJohn eqv otherJohn) shouldBe false
  }
}
