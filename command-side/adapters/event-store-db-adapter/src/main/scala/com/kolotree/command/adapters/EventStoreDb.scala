package com.kolotree.command.adapters

import akka.actor.ActorSystem
import cats.Monad
import com.kolotree.command.common.AggregateRoot
import com.kolotree.command.common.validation.{AggregateAlreadyInStoreException, AggregateVersionMismatch, BaseError}
import com.kolotree.command.ports.EventStore
import eventstore.akka.{EsConnection, Settings}
import eventstore.core.UserCredentials
import monix.eval.Task

import java.net.InetSocketAddress

abstract class EventStoreDb[T <: AggregateRoot[T]] extends EventStore[Task, T] with EventStoreDbAppender {

  protected def actorSystem: ActorSystem
  protected def hostname: String
  protected def port: Int
  protected def username: String
  protected def password: String
  protected override def storeName: String

  override protected val esConnection: EsConnection = EsConnection(
    actorSystem,
    Settings(
      address = new InetSocketAddress(hostname, port),
      defaultCredentials = Some(UserCredentials(username, password)),
      maxReconnections = -1
    )
  )

  override def insert(newAggregate: T): Task[Unit] =
    append(
      newAggregate.id,
      newAggregate.uncommittedEvents,
      -1
    )
      .onErrorRecoverWith { case _: AggregateVersionMismatch =>
        Task.raiseError(AggregateAlreadyInStoreException(newAggregate.id))
      }

  override def borrow(id: String, transformer: T => Either[BaseError, T])(implicit F: Monad[Task]): Task[Unit] =
    borrowInternal(id, retrospective => Task(transformer(retrospective)))
      .onErrorRecoverWith { case _: AggregateVersionMismatch =>
        borrowInternal(id, retrospective => Task(transformer(retrospective)))
      }

  override def borrowAsync(id: String, transformer: T => Task[Either[BaseError, T]]): Task[Unit] =
    borrowInternal(id, transformer)
      .onErrorRecoverWith { case _: AggregateVersionMismatch =>
        borrowInternal(id, transformer)
      }
}
