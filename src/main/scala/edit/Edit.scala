package edit

import javafx.application._
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import java.io.File
import edit.plugins.PluginManager

class Edit extends Application {

  def start(stage: Stage) {
    Edit.stage = stage

    val defaultFont = Font.loadFont("file:resources/fonts/Inconsolata.otf", 14)

    stage.setTitle("edit (loading...)")
    stage.setWidth(800)
    stage.setHeight(600)

    val doc = new Document()
    val editor = new Editor(doc)
    val root = new StackPane()
    root.getChildren.add(editor)

    val scene = new Scene(root)
    scene.getStylesheets.add("file:resources/styles/default.css")
    stage.setScene(scene)

    stage.show()
    editor.init()

    // check if a file should be opened
    if (getParameters.getRaw.size() > 0) {
      val file = new File(getParameters.getRaw.get(0))
      editor.load(file)
    }

    editor.requestFocus()

    val interface = new edit.interfaces.Edit {
      def openFile(path: String) {
        editor.load(new File(path))
      }

      def setWindowTitle(title: String) {
        stage.setTitle(title)
      }

      def getDocument = doc
    }

    Edit.interface = interface
    PluginManager.loadPlugins("resources/plugins")
  }
}

object Edit {

  var stage: Stage = null
  var interface: edit.interfaces.Edit = null

  def main(args: Array[String]) {
    Application.launch(classOf[Edit], args: _*)
  }
}