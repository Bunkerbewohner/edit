package edit.plugins

import java.io.File
import java.util.concurrent.FutureTask

object PluginManager {

  def listPlugins(directory: String) = {
    val dir = new File(directory)
    if (!dir.exists()) throw new Exception(s"Plugin directory '$directory' doesn't exist.")
    if (!dir.canRead) throw new Exception(s"Plugin directory '$directory' cannot be read.")

    dir.listFiles().filter(f => f.isDirectory).map(new Plugin(_))
  }

  def loadPlugins(directory: String) {
    val plugins = listPlugins(directory)
    plugins.foreach(p =>  {
      val thread = new Thread(new Runnable() {
        def run() {
          p.load()
        }
      })

      thread.start()
    })
  }
}
