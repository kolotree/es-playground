package com.kolotree.command.common

import com.kolotree.command.common.AggregateRoot.INITIAL_VERSION
import com.kolotree.command.common.TestEvents.PersonCreatedEvent
import com.kolotree.common.eventing.Event

object TestValues {

  case class Person private (
      id: String,
      firstName: String,
      lastName: String,
      uncommittedEvents: List[Event],
      version: Int
  ) extends AggregateRoot[Person] {

    override protected def incrementVersion(): Person =
      copy(version = version + 1)

    override def applyEvent(event: Event): Person =
      copy()

    override protected def markEventsAsCommitted(): Person =
      copy(uncommittedEvents = List.empty[Event])
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
