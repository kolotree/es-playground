package com.kolotree.command.common

import com.kolotree.command.common.TestEvents.{PersonCreatedEvent, PersonNameChanged}
import com.kolotree.command.common.TestValues.Person
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AggregateStateUpdateSpec extends AnyFlatSpec with Matchers {

  "Aggregate state change" should "generate uncommitted event" in {
    val id = "123"
    val jack = "Jack"
    val person = Person
      .create(id)
      .changeFirstName(jack)

    person.firstName shouldBe jack
    person.uncommittedEvents should contain inOrder (PersonCreatedEvent(id), PersonNameChanged(id, jack))
    person.version shouldBe 1
  }
}
