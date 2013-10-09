package test

import org.specs2.mutable._

import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.libs.ws.WS
import play.api.libs.iteratee.{Enumeratee, Iteratee}
import play.api.libs.json.{Json, JsValue}
import java.lang.Thread
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

/** simpler to write messages using case classes than in JSON directly */
case class Msg(room: String, text: String, user: String, date: String)
object Msg { implicit val msgWriter = Json.writes[Msg] }

class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) {
        browser =>
          browser.goTo("http://localhost:3333/")
          browser.pageSource must contain("Your Name:")
      }

      "deliver JSON over SSE, filtered by chatRoom" in {
        running(TestServer(3333)) {

          /** Folding Iteratee, parses Array[Byte] chunks and accumulates them into a Seq[JsValue] */
          def concatChunks = Iteratee.fold[Array[Byte], Seq[JsValue]](Seq[JsValue]()) {
            (acc, chunk) => acc :+ Json.parse((new String(chunk, "UTF-8")).replace("data: ", ""))
          }

          /** Iteratee above, chained with Enumeratee to reach Done state after n steps*/
          def take(n: Int) = Enumeratee.take(n) &>> concatChunks
          val n = 3 // number of elements for Iteratee to consume
          val chatRoom = "room1"

          /** Test data:  */
          val msgs = Seq(
            Json.toJson(Msg("room1", "message1", "user", "date")),
            Json.toJson(Msg("room2", "message2", "user", "date")),
            Json.toJson(Msg("room1", "message3", "user", "date")),
            Json.toJson(Msg("room3", "message4", "user", "date")),
            Json.toJson(Msg("room1", "message5", "user", "date")),
            Json.toJson(Msg("room1", "message6", "user", "date"))
          )

          /** workaround for problems matching within await{}.map{} */
          var resultSeq = Seq[JsValue]()

          /** start stream consuming connection for chat room feed */
          val client = WS.url("http://localhost:3333/chatFeed/" + chatRoom).get(_ => take(n))

          /** post six messages from Seq above in chat endpoint */
          msgs.foreach( msg => { Thread.sleep(250); WS.url("http://localhost:3333/chat").post(msg) } )

          /** await result of take(n) Iteratee, map result to resultSeq for comparison when done */
          Await.result(client, Duration(5, "seconds")).map { seq => resultSeq = seq }

          /** result must be equal to n elements taken from msgs, filtered by chatRoom */
          resultSeq.length must beEqualTo(n)
          resultSeq must beEqualTo(msgs.filter(json => (json \ "room").as[String] == chatRoom).take(n))
        }
      }
    }
  }
}
