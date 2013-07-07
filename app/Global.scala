import akka.ChatActors
import play.api.GlobalSettings
import utilities.Logger

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    Logger.log("/", "Application started", "INFO", None)
    ChatActors
  }
  
  /** performed when application stops. Use Ctrl-D for stopping the application in dev mode if you want to 
    * trigger onStop, not Ctrl-C */
  override def onStop(application: play.api.Application) {
    Logger.log("/", "Application stopped", "INFO", None)   
    ChatActors.system.shutdown()
  }
}
