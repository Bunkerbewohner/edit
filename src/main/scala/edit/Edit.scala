package edit

import javafx.application._
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.text.Font

class Edit extends Application {
  def start(stage: Stage) {

    val defaultFont = Font.loadFont("file:resources/fonts/Inconsolata.otf", 14)

    stage.setTitle("edit")
    stage.setWidth(800)
    stage.setHeight(600)

    val doc = new Document()
    doc.insert("Hello World!\n\n\tdef start(stage: Stage) {\n\t\tstage.setTitle(\"edit\"))")

    val editor = new Editor(doc)
    val root = new StackPane()
    root.getChildren.add(editor)

    val scene = new Scene(root)
    scene.getStylesheets.add("file:resources/styles/default.css")
    stage.setScene(scene)

    stage.show()
    editor.init()

    editor.requestFocus()
  }
}

object Edit {
  def main(args: Array[String]) {
    Application.launch(classOf[Edit], args: _*)
  }
}