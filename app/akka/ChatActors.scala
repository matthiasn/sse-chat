package akka

import akka.actor._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import scala.language.postfixOps
import scala.concurrent.duration._

import controllers.ChatApplication
import org.joda.time.DateTime
import scala.util.Random

object ChatActors {
  
  /** SSE-Chat actor system */
  val system = ActorSystem("sse-chat")

  /** Supervisor for Romeo and Juliet */
  val supervisor = system.actorOf(Props(new Supervisor()), "ChatterSupervisor")

  case object Talk
}

/** Supervisor initiating Romeo and Juliet actors and scheduling their talking */
class Supervisor() extends Actor {

  val juliet = context.actorOf(Props(new Chatter("Juliet", Quotes.juliet)))
  context.system.scheduler.schedule(1 seconds, 10 seconds, juliet, ChatActors.Talk)
  
  val romeo = context.actorOf(Props(new Chatter("Romeo", Quotes.romeo)))
  context.system.scheduler.schedule(6 seconds, 10 seconds, romeo, ChatActors.Talk)

  def receive = { case _ => }
}

/** Chat participant actors picking quotes at random when told to talk */
class Chatter(name: String, quotes: Seq[String]) extends Actor {
  
  def receive = {
    case ChatActors.Talk  => {
      val now = DateTime.now.toString()
      val quote = quotes(Random.nextInt(quotes.size))
      val msg = Json.obj("room" -> "room1", "text" -> quote, "user" ->  name, "time" -> now )

      ChatApplication.chatChannel.push(msg)
    }
  }
} 

object Quotes {
  val juliet = Seq("O Romeo, Romeo! wherefore art thou Romeo? \nDeny thy father and refuse thy name; \nOr, if thou wilt not, be but sworn my love, \nAnd I'll no longer be a Capulet. ", "By whose direction found'st thou out this place?", "I would not for the world they saw thee here.", "What man art thou that, thus bescreened in night, \nSo stumblest on my counsel?", "If they do see thee, they will murder thee.", "My ears have yet not drunk a hundred words \n Of thy tongue's uttering, yet I know the sound.\nArt thou not Romeo, and a Montague?", "How cam'st thou hither, tell me, and wherefore?\nThe orchard walls are high and hard to climb,\nAnd the place death, considering who thou art,\nIf any of my kinsmen find thee here.")

  val romeo = Seq("Neither, fair saint, if either thee dislike.", "With love's light wings did I o'erperch these walls,\nFor stony limits cannot hold love out,\nAnd what love can do, that dares love attempt:\n Therefore thy kinsmen are no stop to me.", "Alack, there lies more peril in thine eye \nThan twenty of their swords. Look thou but sweet\nAnd I am proof against their enmity.", "By a name\nI know not how to tell thee who I am:\nMy name, dear saint, is hateful to myself,\nBecause it is an enemy to thee.\nHad I it written, I would tear the word.", "I have night's cloak to hide me from their eyes, \nAnd, but thou love me, let them find me here;\nMy life were better ended by their hate\nThan death prorogued, wanting of thy love.", "I take thee at thy word.\n Call me but love, and I'll be new baptis'd;\nHenceforth I never will be Romeo.", "By love, that first did prompt me to enquire.\n He lent me counsel, and I lent him eyes.\n I am no pilot, yet, wert thou as far\nAs that vast shore wash'd with the furthest sea, \nI should adventure for such merchandise.", "[Aside.] Shall I hear more, or shall I speak at this?")
}