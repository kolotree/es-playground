package com.kolotree.command.common

import com.kolotree.common.eventing.Event

object TestEvents {

  case class PersonCreatedEvent(id: String) extends Event
}
