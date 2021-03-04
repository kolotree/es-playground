package com.kolotree.command.common

import com.kolotree.common.eventing.Event

object TestEvents {

  case class PersonCreatedEvent(id: String) extends Event

  case class PersonNameChanged(id: String, name: String) extends Event

  case class PersonLastNameChanged(id: String, lastName: String) extends Event
}
