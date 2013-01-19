package com.risertech.vertx.assets

import org.vertx.java.deploy.Verticle
import org.vertx.java.core.Handler
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import scala.collection.JavaConversions._
import java.security.MessageDigest
import java.net.URI
import org.apache.commons.io.FilenameUtils

class Assets extends Verticle with Handler[Message[JsonObject]] {
  override def start() {}
  
  override def handle(message: Message[JsonObject]) {
    message.body.getField("action") match {
      case "addLessAsset" => addLessAsset(message)
      case "addJadeAsset" => addJadeAsset(message)
      case "addBrowserifyAsset" => addBrowserifyAsset(message)
      case unknown => message.reply(createJsonError("UNKNOWN_COMMAND", s"Assets does not understand the action '$unknown'."))
    }
  }
  
  def addLessAsset(message: Message[JsonObject]) {
    val asset = new LessAsset(
    		message.body.getString("url"),
    		message.body.getString("filename"),
    		message.body.getString("paths"))
    
    message.reply(new JsonObject().putBoolean("added", true))
  }
  
  def addJadeAsset(message: Message[JsonObject]) {
    val asset = new JadeAsset(
    		message.body.getString("url"),
    		message.body.getString("dirname"),
    		message.body.getString("separator"),
    		message.body.getString("clientVariable"))
    
    message.reply(new JsonObject().putBoolean("added", true))
  }
  
  def addBrowserifyAsset(message: Message[JsonObject]) {
    val asset = new BrowserifyAsset(
    		message.body.getString("url"),
    		message.body.getString("filename"),
    		message.body.getBoolean("debug"))
    
    message.reply(new JsonObject().putBoolean("added", true))
  }
  
  private def createJsonError(error: String, message: String) = {
    new JsonObject().putString("status", "error").putString("error", error).putString("message", message)
  }
  
  abstract class Asset(url: String, mimeType: String) {
    def create
    // TODO This will hook into the http server and provide a hashed url for the file
    def createURL: String = {
      val digest = MessageDigest.getInstance("MD5")
      digest.update(Array[Byte]())
      val md5 = digest.digest
      
      val uri = new URI(url)
      val extension = FilenameUtils.getExtension(uri.getPath)
      val filename = FilenameUtils.removeExtension(uri.getPath)
      val hashedURL = s"$filename-$md5.$extension"
      
      if (uri.getHost != null) s"//${uri.getHost}/$hashedURL" else hashedURL
    }
    
    def tag: String = {
      val url = createURL
      mimeType match {
        case "text/javascript" => "<script type=\"" + mimeType + "\" src=\"" + url + "\"></script>"
        case "text/css" => "<link rel=\"stylesheet\" href=\"" + url + "\">"
        case _ => url
      }
    }
  }
  
  class JadeAsset(url: String, dirname: String, separator: String, clientVariable: String) extends Asset(url, "text/javascript") {
    def create = {}
  }
  
  class BrowserifyAsset(url: String, filename: String, debug: Boolean) extends Asset(url, "text/javascript") {
    def create = {}
  }
  
  class LessAsset(url: String, filename: String, paths: String) extends Asset(url, "text/css") {
    def create = {}
  }
}
