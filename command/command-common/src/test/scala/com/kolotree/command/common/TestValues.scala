package com.kolotree.command.common

import com.kolotree.command.common.AggregateRoot.INITIAL_VERSION
import com.kolotree.command.common.TestEvents.{PersonCreatedEvent, PersonLastNameChanged, PersonNameChanged}
import com.kolotree.common.eventing.Event

object TestValues {

  case class Person private (
      id: String,
      firstName: String,
      lastName: String,
      uncommittedEvents: List[Event],
      version: Int
  ) extends AggregateRoot[Person] {

    //TODO: Validate input before applying event
    def changeFirstName(newName: String): Person =
      applyEvent(PersonNameChanged(id, newName))

    override protected def incrementVersion(): Person =
      copy(version = version + 1)

    override protected def applyEvent(event: Event): Person = {
      applyEventInternal(event).incrementVersion()
    }

    override protected def markEventsAsCommitted(): Person =
      copy(uncommittedEvents = List.empty[Event])

    override protected def applyEventInternal(event: Event): Person = {
      val person = event match {
        case PersonNameChanged(_, updatedName) =>
          copy(firstName = updatedName)
        case PersonLastNameChanged(_, updatedLastName) =>
          copy(lastName = updatedLastName)
        case _ => throw new Exception("Unsupported event type")
      }

      person
        .copy(uncommittedEvents = uncommittedEvents :+ event)
    }
  }

  object Person extends AggregateReconstruct[Person] with EventApplier[Person] {

    def create(id: String): Person =
      applyEvent(PersonCreatedEvent(id))

    override protected def applyEvent(event: Event): Person = event match {
      case PersonCreatedEvent(id) =>
        Person(id, "", "", event :: Nil, INITIAL_VERSION)
    }
  }
}
