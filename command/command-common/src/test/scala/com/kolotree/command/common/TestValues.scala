package com.kolotree.command.common

import com.kolotree.command.common.AggregateRoot.INITIAL_VERSION
import com.kolotree.common.eventing.Event

object TestValues {
  case class Person private (
      id: String,
      firstName: String,
      lastName: String,
      uncommittedEvents: List[Event],
      version: Int
  ) extends AggregateRoot[Person] {

    def this(id: String) {
      this(id, "", "", List.empty[Event], INITIAL_VERSION)
    }

    override protected def initialize(id: String): Person = ???

    override protected def incrementVersion(): Person =
      copy(version = version + 1)

    override def applyEvent(event: Event): Person = copy()
  }

  object Person extends AggregateReconstruct[Person] with EventApplier[Person] {
    def apply(id: String) = new Person(id)

    override def applyEvent(event: Event): Person = ???
  }
}
