package com.kolotree.command.common

import com.kolotree.common.eventing.Event

trait EventApplier[T <: AggregateRoot[T]] {
  def applyEvent(event: Event): T
}

trait AggregateReconstruct[T <: AggregateRoot[T]] { self: EventApplier[T] =>
  def load(events: List[Event]): T =
    applyEvent(events.head).load(events.tail)
}

trait AggregateRoot[T <: AggregateRoot[T]]
    extends Identifiable
    with AggregateReconstruct[T]
    with EventApplier[T] {
  self: T =>

  def uncommittedEvents: List[Event]

  def version: Int

  protected def initialize(id: String): T

  protected def incrementVersion(): T

  //TODO: Add events list clear after loading from store
  override def load(events: List[Event]): T =
    events.foldRight(this)((event, root) =>
      root.applyEvent(event).incrementVersion()
    )

  def getPreviousVersion: Int =
    version - uncommittedEvents.size

  override def toString: String =
    s"${this.getClass.getSimpleName} [$id]"
}

object AggregateRoot {
  val INITIAL_VERSION: Int = -1
}
