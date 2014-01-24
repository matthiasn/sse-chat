package com.matthiasnehlsen.sseChat

import scala.scalajs.js
import js.Dynamic.{ global => g }

trait ChatMsgTrait extends js.Object {
  var text: js.String = ???
  var user: js.String = ???
  var time: js.String = ???
  var room: js.String = ???
}

/** Scala representation of SseChatApp JavaScript object holding the JS side of the app */
object SseChatApp extends js.Object {
  def submitMessage(msg: ChatMsgTrait): Unit = ???
  def listen(room: String, handler: js.Function1[ChatMsgTrait, Unit]): Unit = ???
  def setUserProps(user: String): Unit = ???
  def setRoomProps(room: String): Unit = ???
  def setMsgsProps(msgs: js.Array[ChatMsgTrait]): Unit = ???
  def setStackSizeProps(stackSize: String): Unit = ???
  def setApp(interOp: InterOp.type): Unit = ???
  def wireHandlers(submitMsg: js.Function1[ChatMsgTrait, Unit],
                   setUser: js.Function1[String, Unit],
                   setRoom: js.Function1[String, Unit],
                   undo: js.Function0[Unit],
                   undoAll: js.Function1[String, Unit]): Unit = ???
}

object InterOp {
  def addMsg(msg: ChatMsgTrait): Unit = App.addMsg(msg)

  def triggerReact(): Unit = {
    val state = App.stack.head
    SseChatApp.setUserProps(state.user)
    SseChatApp.setRoomProps(state.room)
    SseChatApp.setMsgsProps(state.msgs.toArray[ChatMsgTrait])
    SseChatApp.setStackSizeProps(App.stack.size.toString)
  }

  def setUser(user: String): Unit = App.setUser(user.toString)
  def setRoom(room: String): Unit = App.setRoom(room)

  def submitMsg(msg: ChatMsgTrait) = {
    msg.room = App.stack.head.room
    msg.user = App.stack.head.user
    SseChatApp.submitMessage(msg)
  }

  def undo(): Unit = App.undo()
  def undoAll(interval: String): Unit = App.undoAll(interval.toInt)

  def setTimeout(fn: () => Unit, millis: Int): Unit = g.setTimeout(fn, millis)
}
