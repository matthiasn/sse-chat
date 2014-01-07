package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._

object ChatApplication extends Controller {

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  /** Controller action serving AngularJS chat page */
  def index = Action { Ok(views.html.index("Chat using Server Sent Events and AngularJS")) }

  /** Controller action serving React chat page */
  def indexReact = Action { Ok(views.html.react("Chat using Server Sent Events and React")) }

  /** Controller action serving React chat page */
  def indexReactScalaJS = Action { Ok(views.html.react_scala_js("Chat using Server Sent Events, React and Scala.js")) }

  /** Controller action for POSTing chat messages */
  def postMessage = Action(parse.json) { req => println(req.body); chatChannel.push(req.body); Ok }

  /** Enumeratee for filtering messages based on room */
  def filter(room: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "room").as[String] == room }

  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(addr: String): Enumeratee[JsValue, JsValue] = 
    Enumeratee.onIterateeDone{ () => println(addr + " - SSE disconnected") }

  /** Controller action serving activity based on room */
  def chatFeed(room: String) = Action { req =>
    println(req.remoteAddress + " - SSE connected")
    Ok.feed(chatOut
      &> filter(room) 
      &> Concurrent.buffer(50) 
      &> connDeathWatch(req.remoteAddress)
      &> EventSource()
    ).as("text/event-stream") 
  }

}
