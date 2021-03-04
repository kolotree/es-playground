package com.kolotree.command.ports

import cats.{Monad, MonadError}
import cats.data.EitherT
import com.kolotree.command.common.AggregateRoot
import com.kolotree.command.common.validation.BaseError
import com.kolotree.common.eventing.Event

trait Store[F[_], T <: AggregateRoot[T]] {

  def insert(newAggregate: T): F[T]

  def borrow(id: String, transformer: T => Either[BaseError, T])(implicit F: Monad[F]): F[T] =
    borrowAsync(id, aggregate => F.pure(transformer(aggregate)))

  def borrowAsync(id: String, transformer: T => F[Either[BaseError, T]]): F[T]
}

trait EventAppender[F[_]] {

  protected def append(id: String, domainEvents: List[Event], expectedVersion: Long): F[Unit]

  protected def readAllEventsFor(id: String): F[List[Event]]
}

trait EventStore[F[_], T <: AggregateRoot[T]] {
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

  protected def saveUncommittedEvents(aggregateRoot: T)(implicit F: Monad[F]): F[Unit] =
    if (aggregateRoot.uncommittedEvents.isEmpty) F.pure(())
    else append(aggregateRoot.id, aggregateRoot.uncommittedEvents, aggregateRoot.getPreviousVersion)
}
