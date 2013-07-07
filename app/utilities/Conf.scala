package utilities

import play.api.Play.current
import play.api.libs.oauth.{RequestToken, ConsumerKey}

object Conf {
  
  /** Retrieves a key from the application.conf. Throws exception if key not found.
   *  @param    key String configuration key
   *  @return   String with configuration key
   */
  def get(key: String): String = current.configuration.getString(key)
    .getOrElse(throw new Exception("Couldn't find config for " + key))

  def getOrEmpty(key: String): String = current.configuration.getString(key).getOrElse("")
}