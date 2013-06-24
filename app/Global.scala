import akka.ChatActors
import play.api.GlobalSettings

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    ChatActors
  }
  
  override def onStop(application: play.api.Application) { 
    ChatActors.system.shutdown()
  }
}
