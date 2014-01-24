package com.matthiasnehlsen.sseChat

import com.matthiasnehlsen.sseChat.ChangeAwareStack

// custom stack implementation based on mutable Stack for any type T
// takes callback function argument, which it will call on changes with the current head after the change
class ChangeAwareStack[T](onChange: () => Unit) extends scala.collection.mutable.Stack[T] {

  override def push(elem: T) = {
    val res = super.push(elem)
    onChange()
    res
  }

  override def pop() = {
    val res = super.pop()
    onChange()
    res
  }

  def peek = super.head  // convenience method since stack implementation does not implement peek()
}

object ChangeAwareStack {
  def apply[T](onChange: () => Unit) = new ChangeAwareStack[T](onChange)
}
