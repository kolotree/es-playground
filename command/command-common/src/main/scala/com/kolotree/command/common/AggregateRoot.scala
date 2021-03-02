package com.kolotree.command.common

import com.kolotree.common.eventing.Event

trait AggregateRoot[T <: AggregateRoot[T]] extends Identifiable {

  def uncommittedEvents: List[Event]

  def version: Int

  private[common] def incrementVersion(): T = incrementVersionInternal()

  protected def incrementVersionInternal(): T

  protected def applyInternal(event: Event): T

  def getPreviousVersion: Int =
    version - uncommittedEvents.size

  override def toString: String =
    s"${this.getClass.getSimpleName} [$id]"
}
