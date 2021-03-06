package com.kolotree.command.common

import com.kolotree.common.eventing.Event

trait EventApplier[T <: AggregateRoot[T]] {
  protected def applyEvent(event: Event): T
}

trait AggregateReconstruct[T <: AggregateRoot[T]] {
  self: EventApplier[T] =>

  def loadFromHistory(events: List[Event]): T =
    applyEvent(events.head).loadFromHistory(events.tail)
}

trait AggregateRoot[T <: AggregateRoot[T]] extends Identifiable with EventApplier[T] with AggregateReconstruct[T] {
  self: T =>

  def uncommittedEvents: List[Event]

  def version: Int

  protected def incrementVersion(): T

  protected def applyEventInternal(event: Event): T

  override protected def applyEvent(event: Event): T =
    applyEventInternal(event).incrementVersion()

  override def loadFromHistory(events: List[Event]): T =
    events
      .foldRight(this) { (event, root) =>
        root.applyEventInternal(event).incrementVersion()
      }
      .markEventsAsCommitted()

  protected def markEventsAsCommitted(): T

  def getPreviousVersion: Int =
    version - uncommittedEvents.size

  override def toString: String =
    s"${this.getClass.getSimpleName} [$id]"
}

object AggregateRoot {
  val INITIAL_VERSION: Int = 0
}
