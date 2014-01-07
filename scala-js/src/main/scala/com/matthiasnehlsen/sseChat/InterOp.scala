package com.matthiasnehlsen.sseChat

import scala.scalajs.js

trait ChatMsg extends js.Object {
  val text: js.String = ???
  val user: js.String = ???
  val time: js.String = ???
  val room: js.String = ???
}

case class UIState(user: String, room: String, msgs: js.Array[Msg])

object JSON extends js.Object {
  def parse(jsonString: js.String): js.Object = ???
  def stringify(json: js.Object): js.String = ???
}

object SseChatApp extends js.Object {
  def submitMessage(msg: js.Object): Unit = ???
  def listen(room: String, handler: js.Function1[ChatMsg, Unit]) = ???
  def setAppState(state: UIState): Unit = ???
}

object InterOp {

  def triggerReact(): Unit = {
    val latest = App.stack.head
    SseChatApp.setAppState(UIState(latest.user, latest.room, latest.msgVector.toArray[Msg]))
  }

  def setUser(user: js.String) = App.setUser(user)
  def setRoom(room: String) = App.setRoom(room)

  def undo() = App.undo()

}
