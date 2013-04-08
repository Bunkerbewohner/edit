package edit.plugins

import java.io.File
import java.util.concurrent.FutureTask

object PluginManager {

  protected var _plugins = Array[Plugin]()

  def listPlugins(directory: String) = {
    val dir = new File(directory)
    if (!dir.exists()) throw new Exception(s"Plugin directory '$directory' doesn't exist.")
    if (!dir.canRead) throw new Exception(s"Plugin directory '$directory' cannot be read.")

    dir.listFiles().filter(f => f.isDirectory).map(new Plugin(_))
  }

  def loadPlugins(directory: String) {
    _plugins = listPlugins(directory)
    _plugins.foreach(p =>  {
      val thread = new Thread(new Runnable() {
        def run() {
          p.load()
        }
      })

      thread.start()
    })
  }

  def reload() {
    println("reloading plugins...")
    _plugins.foreach(p => {
      new Thread(new Runnable() {
        def run() {
          p.load()
        }
      }).start()
    })
  }
}
