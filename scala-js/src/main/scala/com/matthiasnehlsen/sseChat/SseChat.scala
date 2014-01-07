package com.matthiasnehlsen.sseChat

import scala.collection.immutable._

case class Msg(text: String, user: String, time: String, room: String)
case class AppState(user: String, room: String, msgVector: Vector[Msg])

object App {

  val r = new scala.util.Random() // for random generation of user name

  val initialState = AppState("Jane Doe #" + r.nextInt(100), "room1", Vector[Msg]())
  val stack = scala.collection.mutable.Stack[AppState](initialState)

  def undo(): Unit = {
    if (stack.size > 1) {
      stack.pop()
      InterOp.triggerReact()
    }
  }

  def updateState(state: AppState): Unit = {
    stack.push(state)
    InterOp.triggerReact()
  }

  def setRoom(newRoom: String): Unit = updateState(stack.head.copy(room = newRoom))
  def setUser(name: String): Unit = updateState(stack.head.copy(user = name))
  def addMsg(msg: ChatMsg): Unit = {
    val typedMsg = Msg(msg.text, msg.user, msg.time, msg.room)
    updateState(stack.head.copy(msgVector = stack.head.msgVector.takeRight(3) :+ typedMsg))
  }

  def main(): Unit = SseChatApp.listen(stack.head.room, addMsg _)

  /** Computes the square of an integer.
    *  This demonstrates unit testing.
    */
  def square(x: Int): Int = x*x
}
