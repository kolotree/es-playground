package com.kolotree.command.common

import com.kolotree.common.eventing.Event

object TestValues {
  case class Person(id: String,
                    firstName: String,
                    lastName: String,
                    uncommittedEvents: List[Event],
                    version: Int)
    extends AggregateRoot[Person] {

    override protected def incrementVersionInternal(): Person = copy(version = version + 1)

    override protected def applyInternal(event: Event): Person = copy()
  }
}
