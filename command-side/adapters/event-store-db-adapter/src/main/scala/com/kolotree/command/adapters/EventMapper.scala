package com.kolotree.command.adapters

import java.util.UUID

import com.kolotree.common.eventing.Event
import eventstore.{Content, EventData}
import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.json4s.native.Serialization.read
import org.json4s.native.Serialization.write

import scala.reflect.ManifestFactory

case class EventMetadata(className: String)

object EventMapper {
  implicit val formats: DefaultFormats = DefaultFormats

  def toEventData(event: Event): EventData = {
    EventData(
      event.getClass.getSimpleName,
      UUID.randomUUID(),
      Content.Json(write(event)),
      Content.Json(write(EventMetadata(event.getClass.getCanonicalName)))
    )
  }

  def toDomainEvent(eventStoreEvent: eventstore.Event): Event = {
    val eventData = eventStoreEvent.data

    val domainEventString = Content.Json.unapply(eventData.data).get

    val domainEventMetadataString = Content.Json.unapply(eventData.metadata).get
    val className = resolveClassName(domainEventMetadataString)

    implicit val formats: DefaultFormats = DefaultFormats
    implicit val m: Manifest[Nothing] = ManifestFactory.classType(Class.forName(className))

    parse(domainEventString).extract
  }

  def resolveClassName(domainEventMetadataString: String): String = {
    read[EventMetadata](domainEventMetadataString).className
  }

}
