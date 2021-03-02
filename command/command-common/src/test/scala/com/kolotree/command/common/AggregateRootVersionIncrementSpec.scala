package com.kolotree.command.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import TestValues._
import com.kolotree.common.eventing.Event
import org.scalatest.PrivateMethodTester

class AggregateRootVersionIncrementSpec extends AnyFlatSpec with Matchers with PrivateMethodTester {

  "Incrementing version on aggregate" should "increase version by 1" in {
    val john = Person("123", "John", "Doe", List.empty[Event], -1)

    john.incrementVersion().version shouldBe 0
  }

}
