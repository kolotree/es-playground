package com.kolotree.command.common

import com.kolotree.command.common.TestEvents.PersonCreatedEvent
import com.kolotree.command.common.TestValues.Person
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.enablers.Emptiness.emptinessOfAnyRefWithParameterlessIsEmptyMethod

class AggregateCreationSpec extends AnyFlatSpec with Matchers {

  "Created aggregate" should "have one uncommitted event" in {
    val id = "123"
    val personAggregate = Person.create(id)
    personAggregate.id shouldBe id
    personAggregate.uncommittedEvents should contain only PersonCreatedEvent(id)
  }

  "Reconstructed aggregate" should "not have uncommitted events" in {
    val id = "123"
    val events = List(PersonCreatedEvent(id))
    val personAggregate = Person.loadFromHistory(events)
    personAggregate.uncommittedEvents shouldBe empty
  }
}
