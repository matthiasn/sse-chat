package com.matthiasnehlsen.sseChat

import scala.scalajs.js
import js.Dynamic.{ global => g }

trait ChatMsgTrait extends js.Object {
  var text: js.String = ???
  var user: js.String = ???
  var time: js.String = ???
  var room: js.String = ???
}

class ChatMsg extends ChatMsgTrait

// UI state, with Array[TypedMsg] for easy parsing from ReactJS
case class UIState(user: String, room: String, msgs: js.Array[TypedMsg], stackSize: Int)

object JSON extends js.Object {
  def parse(jsonString: js.String): js.Object = ???
  def stringify(json: js.Object): js.String = ???
}

/** Scala representation of SseChatApp JavaScript object holding the JS side of the app */
object SseChatApp extends js.Object {
  def submitMessage(msg: ChatMsg): Unit = ???
  def listen(room: String, handler: js.Function1[ChatMsg, Unit]): Unit = ???
  def setAppState(state: UIState): Unit = ???
}

object InterOp {
  import AppImplicits._

  def addMsg(msg: ChatMsg): Unit = App.addMsg(msg)

  def triggerReact(state: AppState): Unit = SseChatApp.setAppState(state)

  def setUser(user: js.String) = App.setUser(user)
  def setRoom(room: String) = App.setRoom(room)

  def submitMsg(msg: ChatMsg) = {
    msg.room = App.stack.head.room
    msg.user = App.stack.head.user
    SseChatApp.submitMessage(msg)
  }

  def undo() = App.undo()
  def undoAll(interval: Int) = App.undoAll(interval)

  def setTimeout(fn: () => Unit, millis: Int): Unit = g.setTimeout(fn, millis)

}
