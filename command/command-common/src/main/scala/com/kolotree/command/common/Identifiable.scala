package com.kolotree.command.common

import cats.Eq

trait Identifiable {
  def id: String
}

object Identifiable {
  implicit def idEquals[T <: Identifiable]: Eq[T] = (a: T, b: T) => a.id == b.id
}
