package com.kolotree.common.eventing

trait Message

trait Event extends Message {
  def id: String
}
