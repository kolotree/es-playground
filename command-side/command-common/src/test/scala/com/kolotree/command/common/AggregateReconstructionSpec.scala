package com.kolotree.command.common

import com.kolotree.command.common.TestEvents.{
  PersonCreatedEvent,
  PersonLastNameChanged,
  PersonNameChanged
}
import com.kolotree.command.common.TestValues.Person
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.enablers.Emptiness.emptinessOfAnyRefWithParameterlessIsEmptyMethod

class AggregateReconstructionSpec extends AnyFlatSpec with Matchers {

  "Reconstructed aggregate" should "have appropriate state" in {
    val id = "123"
    val updatedName = "Jack"
    val updatedLastName = "James"
    val events = List(
      PersonCreatedEvent(id),
      PersonNameChanged(id, updatedName),
      PersonLastNameChanged(id, updatedLastName)
    )

    val person = Person.loadFromHistory(events)
    person.firstName shouldBe updatedName
    person.lastName shouldBe updatedLastName
    person.id shouldBe id
    person.version shouldBe 2
    person.uncommittedEvents shouldBe empty
  }
}
