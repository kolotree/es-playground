package com.kolotree.command.ports

import cats.MonadError
import cats.data.EitherT
import com.kolotree.command.common.AggregateRoot
import com.kolotree.command.common.validation.{AggregateAlreadyInStoreException, AggregateVersionMismatch, BaseError}
import com.kolotree.common.eventing.Event
import cats.implicits._

//TODO: Store methods should return aggregate without uncommitted events, instead of unit
trait Store[F[_], T <: AggregateRoot[T]] {

  def insert(newAggregate: T)(implicit F: MonadError[F, Throwable]): F[Unit]

  def borrow(id: String, transformer: T => Either[BaseError, T])(implicit F: MonadError[F, Throwable]): F[Unit] =
    borrowAsync(id, aggregate => F.pure(transformer(aggregate)))

  def borrowAsync(id: String, transformer: T => F[Either[BaseError, T]])(implicit F: MonadError[F, Throwable]): F[Unit]
}

trait EventAppender[F[_]] {

  protected def append(id: String, domainEvents: List[Event], expectedVersion: Long): F[Unit]

  protected def readAllEventsFor(id: String): F[List[Event]]
}

trait EventStore[F[_], T <: AggregateRoot[T]] extends Store[F, T] {
  self: EventAppender[F] =>

  protected def reconstruct: List[Event] => T

  protected def reconstructAggregate(events: List[Event]): T =
    reconstruct(events)

  protected def borrowInternal(id: String, transformer: T => F[Either[BaseError, T]])(implicit F: MonadError[F, Throwable]): F[Unit] = {
    val transformationEither = for {
      events <- EitherT.liftF[F, BaseError, List[Event]](readAllEventsFor(id))
      aggregate <- EitherT.pure[F, BaseError](reconstructAggregate(events))
      aggregateWithChanges <- EitherT(transformer(aggregate))
      _ <- EitherT.liftF[F, BaseError, Unit](saveUncommittedEvents(aggregateWithChanges))
    } yield ()

    transformationEither
      .foldF(
        error => F.raiseError(new Exception(error.message)),
        _ => F.pure(())
      )
  }

  protected def saveUncommittedEvents(aggregateRoot: T)(implicit F: MonadError[F, Throwable]): F[Unit] =
    if (aggregateRoot.uncommittedEvents.isEmpty) F.pure(())
    else append(aggregateRoot.id, aggregateRoot.uncommittedEvents, aggregateRoot.getPreviousVersion)

  override def borrowAsync(id: String, transformer: T => F[Either[BaseError, T]])(implicit F: MonadError[F, Throwable]): F[Unit] =
    borrowInternal(id, transformer)
      .handleErrorWith { case _: AggregateVersionMismatch =>
        borrowInternal(id, transformer)
      }

  def insert(newAggregate: T)(implicit F: MonadError[F, Throwable]): F[Unit] =
    append(
      newAggregate.id,
      newAggregate.uncommittedEvents,
      -1
    ).handleErrorWith { case _: AggregateVersionMismatch =>
      F.raiseError(AggregateAlreadyInStoreException(newAggregate.id))
    }

}
