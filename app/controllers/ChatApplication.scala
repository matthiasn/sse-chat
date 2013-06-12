package controllers

import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.iteratee.{Enumeratee, Concurrent}
import play.api.libs.EventSource

object ChatApplication extends Controller {

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[JsValue]

  /** Controller action serving chat page */
  def index = Action { Ok(views.html.index("Chat using Server Sent Events")) }

  /** Controller action for POSTing chat messages */
  def postMessage = Action(parse.json) { req => chatChannel.push(req.body); Ok }

  /** Enumeratee for filtering messages based on room */
  def filter(room: String) = Enumeratee.filter[JsValue] { json: JsValue => (json \ "room").as[String] == room }

  /** Controller action serving activity based on room */
  def chatFeed(room: String) = Action { Ok.stream(chatOut &> filter(room) &> EventSource()).as("text/event-stream") }

}