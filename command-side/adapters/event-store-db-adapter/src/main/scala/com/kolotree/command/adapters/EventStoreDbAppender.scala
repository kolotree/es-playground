package com.kolotree.command.adapters

import com.kolotree.command.adapters.EventMapper.{toDomainEvent, toEventData}
import com.kolotree.command.common.validation.{AggregateNotFoundException, AggregateVersionMismatch}
import com.kolotree.command.ports.EventAppender
import com.kolotree.common.eventing.Event
import eventstore.akka.EsConnection
import eventstore.core.{EventStream, ExpectedVersion, StreamNotFoundException}
import eventstore.{EventNumber, ReadStreamEvents, WriteEvents, WrongExpectedVersionException}
import monix.eval.Task

trait EventStoreDbAppender extends EventAppender[Task] {
  protected val storeName: String

  protected def esConnection: EsConnection

  override protected def append(id: String, domainEvents: List[Event], expectedVersion: Long): Task[Unit] =
    Task
      .fromFuture(
        esConnection(
          WriteEvents(
            resolveStream(id),
            domainEvents.map(e => toEventData(e)),
            ExpectedVersion(expectedVersion)
          )
        )
      )
      .map(_ => ())
      .onErrorRecoverWith { case _: WrongExpectedVersionException =>
        Task.raiseError(AggregateVersionMismatch(id))
      }

  override protected def readAllEventsFor(id: String): Task[List[Event]] = {
    val streamId = resolveStream(id)
    val batchSize = 4096

    def readInBatches(acc: List[eventstore.Event], currentPosition: Long): Task[List[eventstore.Event]] = {
      Task
        .fromFuture(esConnection(ReadStreamEvents(streamId, EventNumber(currentPosition))))
        .flatMap(readResult =>
          if (readResult.endOfStream) Task {
            acc ++ readResult.events
          }
          else readInBatches(acc ++ readResult.events, currentPosition + batchSize)
        )
    }

    readInBatches(Nil: List[eventstore.Event], 0)
      .map(eventStoreEvents => eventStoreEvents.map(eventStoreEvent => toDomainEvent(eventStoreEvent)))
      .onErrorRecoverWith { case _: StreamNotFoundException =>
        Task.raiseError(AggregateNotFoundException(id))
      }
  }

  protected def resolveStream(id: String): EventStream.Id =
    EventStream.Id(s"$storeName|$id")
}
