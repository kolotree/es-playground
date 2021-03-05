package com.kolotree.command.adapters

import akka.actor.ActorSystem
import com.kolotree.command.common.AggregateRoot
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
}
