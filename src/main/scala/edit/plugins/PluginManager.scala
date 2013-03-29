package main.scala.edit.plugins

import java.io.File

object PluginManager {

  def listPlugins(directory: String) = {
    val dir = new File(directory)
    if (!dir.exists()) throw new Exception(s"Plugin directory '$directory' doesn't exist.")
    if (!dir.canRead) throw new Exception(s"Plugin directory '$directory' cannot be read.")

    dir.listFiles().filter(f => f.isDirectory).map(new Plugin(_))
  }

  def loadPlugins(directory: String) {
    val plugins = listPlugins(directory)
    plugins.foreach(p => p.load())
  }
}
