package com.matthiasnehlsen.sseChat

object Utils {
  val r = new scala.util.Random() // for random generation of user name
  val getInitialState = AppState("Jane Doe #" + r.nextInt(100), "room1", Vector[TypedMsg]())
}
