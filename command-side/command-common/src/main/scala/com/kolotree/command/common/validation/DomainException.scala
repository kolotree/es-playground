package com.kolotree.command.common.validation

sealed case class DomainException(error: BaseError) extends Throwable(error.message)

final case class AggregateNotFoundException(id: String) extends Throwable(s"Aggregate $id not found in store.")

final case class AggregateAlreadyInStoreException(id: String) extends Throwable(s"Aggregate $id already in store.")

final case class AggregateVersionMismatch(id: String) extends Throwable(s"Aggregate $id version mismatch.")
