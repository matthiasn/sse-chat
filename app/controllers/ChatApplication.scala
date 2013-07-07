package controllers

import play.api.mvc._
import play.api.libs.iteratee.{Concurrent, Enumeratee}
import play.api.libs.EventSource
import utilities.Logger
import org.joda.time.{DateTimeZone, DateTime}
import play.api.libs.json.{JsObject, JsValue, Json}

object ChatApplication extends Controller {
  case class Message(json: JsValue, remoteAddress: String)

  /** Central hub for distributing chat messages */
  val (chatOut, chatChannel) = Concurrent.broadcast[Message]

  /** Controller action serving chat page */
  def index = Action { Ok(views.html.index("Chat using Server Sent Events")) }

  /** Controller action for POSTing chat messages */
  def postMessage = Action(parse.json) { req => chatChannel.push(Message(req.body, req.remoteAddress)); Ok }

  /** Enumeratee for filtering messages based on room */
  def logger(req: Request[AnyContent]): Enumeratee[JsValue, JsValue] = Enumeratee.map[JsValue] {
    json: JsValue => {
      Logger.log("/chatFeed/msg", "SSE delivered", "INFO", Some(json.as[JsObject] ++ Json.obj("requestID" -> req.id) ))
      json
    }
  }

  /** calculates milliseconds between passed in DateTime and time of function call */
  def duration(since: DateTime) = DateTime.now.getMillis - since.getMillis
  
  /** Enumeratee for detecting disconnect of SSE stream */
  def connDeathWatch(req: Request[AnyContent], since: DateTime): Enumeratee[JsValue, JsValue] =
    Enumeratee.onIterateeDone { () => Logger.logRequest(req, "SSE disconnected", 200, duration(since))}

  /** Enumeratee for filtering messages based on IP address (in demo messages only delivered to sender) */
  def ipFilter(remoteAddress: String) = Enumeratee.filter[Message] {
    msg => msg.remoteAddress == remoteAddress || msg.remoteAddress == "local" 
  }

  /** map Message case class to JsValue */
  def toJsValue: Enumeratee[Message, JsValue]  = Enumeratee.map[Message] { msg: Message => msg.json }

  /** Enumeratee for filtering messages based on room */
  def roomFilter(room: String) = Enumeratee.filter[Message] { msg => (msg.json \ "room").as[String] == room }
  
  /** Controller action serving activity based on room */
  def chatFeed(room: String) = Action {
    req => { 
      Logger.logRequest(req, "SSE connected", 200, 0)
      
      Ok.stream(chatOut &> roomFilter(room)
        &> ipFilter(req.remoteAddress)
        &> Concurrent.buffer(250)
        &> toJsValue
        &> logger(req)
        &> connDeathWatch(req, new DateTime(DateTimeZone.UTC)  )
        &> EventSource()
      ).as("text/event-stream")
    }
  }
}