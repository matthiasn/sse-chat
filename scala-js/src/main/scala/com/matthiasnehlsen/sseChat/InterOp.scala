package com.matthiasnehlsen.sseChat

import scala.scalajs.js
import js.Dynamic.{ global => g }

trait ChatMsg extends js.Object {
  val text: js.String = ???
  val user: js.String = ???
  val time: js.String = ???
  val room: js.String = ???
}

// UI state, with Array[TypedMsg] for easy parsing from ReactJS
case class UIState(user: String, room: String, msgs: js.Array[TypedMsg], stackSize: Int)

object JSON extends js.Object {
  def parse(jsonString: js.String): js.Object = ???
  def stringify(json: js.Object): js.String = ???
}

/** Scala representation of SseChatApp JavaScript object holding the JS side of the app */
object SseChatApp extends js.Object {
  def submitMessage(msg: js.Object): Unit = ???
  def listen(room: String, handler: js.Function1[ChatMsg, Unit]): ChatMsg = ???
  def setAppState(state: UIState): Unit = ???
}

object InterOp {
  import AppImplicits._

  def addMsg(msg: ChatMsg): Unit = App.addMsg(msg)

  def triggerReact(): Unit = SseChatApp.setAppState(App.stack.head)

  def setUser(user: js.String) = App.setUser(user)
  def setRoom(room: String) = App.setRoom(room)

  def undo() = App.undo()
  def undoAll(interval: Int) = App.undoAll(interval)

  def setTimeout(fn: () => Unit, millis: Int): Unit = g.setTimeout(fn, millis)

}
