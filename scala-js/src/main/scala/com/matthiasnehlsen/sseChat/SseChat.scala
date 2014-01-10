package com.matthiasnehlsen.sseChat

import scala.collection.immutable._

// typed chat message, immutable
case class TypedMsg(text: String, user: String, time: String, room: String)

// current version of application state modeled as immutable case class
case class AppState(user: String, room: String, msgs: Vector[TypedMsg])

object App {

  // Application state history modeled as stack. New versions of state get pushed onto stack.
  // Previous states are available with a combination of pop and peek (called head in Scala implementation)
  val stack = new ChangeAwareStack[AppState](InterOp.triggerReact)
  stack.push(Utils.getInitialState)
  //val stack = scala.collection.mutable.Stack[AppState](Utils.getInitialState)

  // undo state change by popping stack and trigger rendering (which reads the head)
  def undo(all: Boolean = false, interval: Int = 0): Unit = stack.pop()

  // perform undo repeatedly until only initial element left, with interval duration between steps
  def undoAll(interval: Int): Unit = {
    if (stack.size > 1) {
      undo()
      InterOp.setTimeout( () => undoAll(interval), interval)
    }
  }

  // functions generating new version of state which are then pushed onto stack using updateState()
  def setUser(name: String) = stack.push(stack.head.copy(user = name))
  def addMsg(msg: TypedMsg) = stack.push(stack.head.copy(msgs = stack.head.msgs.takeRight(3) :+ msg))
  def setRoom(newRoom: String) = {
    stack.push(stack.head.copy(room = newRoom))
    SseChatApp.listen(stack.head.room, InterOp.addMsg _)
  }

  def main(): Unit = SseChatApp.listen(stack.head.room, InterOp.addMsg _)

  /** Computes the square of an integer.
    *  This demonstrates unit testing.
    */
  def square(x: Int): Int = x*x
}
