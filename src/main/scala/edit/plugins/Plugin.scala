package edit.plugins

import edit.Edit
import org.python.util.PythonInterpreter
import java.io.File

class Plugin(val directory: File) {

  val python = new PythonInterpreter()
  var _loaded = false
  def loaded = _loaded

  def load() {
    val mainFile = new File(directory.getPath + "/" + directory.getName + ".py")
    if (!mainFile.exists()) throw new Exception("Couldn't load '" + mainFile.getAbsolutePath + "'")

    python.set("edit", Edit.interface)
    python.execfile(mainFile.getAbsolutePath)

    _loaded = true
  }
}