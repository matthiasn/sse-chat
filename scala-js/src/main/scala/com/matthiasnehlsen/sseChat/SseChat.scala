package com.matthiasnehlsen.sseChat

import scala.collection.immutable._
import com.matthiasnehlsen.sseChat.ChangeAwareStack
import com.matthiasnehlsen.sseChat.AppState

// current version of application state modeled as immutable case class
case class AppState(user: String, room: String, msgs: Vector[ChatMsgTrait])

object App {
  // Application state history modeled as stack. New versions of state get pushed onto stack.
  // Previous states are available with a combination of pop and peek (called head in Scala implementation)
  val stack = ChangeAwareStack[AppState](InterOp.triggerReact)
  stack.push(Utils.getInitialState)

  // undo state change by popping stack and trigger rendering (which reads the head)
  def undo(all: Boolean = false): Unit = if (stack.size > 1) stack.pop()

  // perform undo repeatedly until only initial element left, with interval duration between steps
  def undoAll(interval: Int): Unit = {
    if (stack.size > 1) {
      undo()
      InterOp.setTimeout( () => undoAll(interval), interval)
    }
  }

  // functions generating new version of state which are then pushed onto stack using updateState()
  def setUser(name: String) = stack.push(stack.head.copy(user = name))
  def addMsg(msg: ChatMsgTrait) = stack.push(stack.head.copy(msgs = stack.head.msgs.takeRight(3) :+ msg))

  def setRoom(newRoom: String) = {
    stack.push(stack.peek.copy(room = newRoom))
    SseChatApp.listen(stack.head.room, InterOp.addMsg _)
  }

  def main(): Unit = {
    SseChatApp.listen(stack.head.room, InterOp.addMsg _)
    SseChatApp.wireHandlers(InterOp.submitMsg _, InterOp.setUser _, InterOp.setRoom _, InterOp.undo _, InterOp.undoAll _)
  }
}
