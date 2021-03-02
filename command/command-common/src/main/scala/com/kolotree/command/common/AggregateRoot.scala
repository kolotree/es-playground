package com.kolotree.command.common

import com.kolotree.common.eventing.Event

trait AggregateRoot extends Identifiable {

  def uncommittedEvents: List[Event]

  def version: Int = -1

  protected def incrementVersion: this.type

  protected def applyInternal(event: Event): this.type

  def getPreviousVersion: Int =
    version - uncommittedEvents.size

  override def toString: String =
    s"${this.getClass.getSimpleName} [$id]"
}
