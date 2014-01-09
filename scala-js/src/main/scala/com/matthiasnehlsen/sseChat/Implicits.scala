package com.matthiasnehlsen.sseChat

object AppImplicits {

  // make Scala aware how to transform AppState to UIState case classes
  implicit def appState2UIState(appState: AppState): UIState = {
    UIState(appState.user, appState.room, appState.msgs.toArray[TypedMsg], App.stack.size)
  }

  // make Scala aware how to transform js.Object adhering to ChatMsg trait to TypedMsg case classes
  implicit def chatMsg2typedMsg(msg: ChatMsg): TypedMsg = {
    TypedMsg(msg.text, msg.user, msg.time, msg.room)
  }
}
