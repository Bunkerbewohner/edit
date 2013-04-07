package edit

import javafx.application._
import javafx.stage.{WindowEvent, Stage}
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import java.io.File
import edit.plugins.PluginManager
import view.{RegisterSyntaxHighlighter, SyntaxHighlighters, SyntaxHighlighter}

class Edit extends Application {

  def onClosing(e: WindowEvent) {
    Signals << Shutdown()
  }

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
    scene.getStylesheets.add("file:resources/styles/syntaxhighlighting-common.css")
    stage.setScene(scene)
    stage.setOnCloseRequest(Events.eventHandler(onClosing))

    stage.show()
    editor.init()

    // check if a file should be opened
    if (getParameters.getRaw.size() > 0) {
      val file = new File(getParameters.getRaw.get(0))
      editor.load(file)
    }

    editor.requestFocus()

    val interface = new edit.interfaces.Edit with Receptor {
      def openFile(path: String) {
        editor.load(new File(path))
      }

      def __lshift__(signal: Signal) {
        Signals << signal
      }

      def setWindowTitle(title: String) {
        stage.setTitle(title)
      }

      def getDocument = doc

      protected def receive(signal: Signal) {
        Platform.runLater(new Runnable() {
          def run() {
            signal match {
              case SetWindowTitle(title) => stage.setTitle(title)
              case OpenDocument(path) => editor.load(new File(path))
              case RegisterStylesheet(path) => scene.getStylesheets.add("file:" + path)
            }
          }
        })
      }
    }

    Edit.interface = interface
    Signals.addReceptor(interface, List(classOf[SetWindowTitle], classOf[OpenDocument], classOf[RegisterStylesheet]))

    SyntaxHighlighters.init()
    PluginManager.loadPlugins("resources/plugins")

    new Thread(new Runnable() {
      def run() {
        interface.reactLoop()
      }
    }).start()
  }
}

object Edit {

  var stage: Stage = null
  var interface: edit.interfaces.Edit = null

  def main(args: Array[String]) {
    Application.launch(classOf[Edit], args: _*)
  }
}

case class SetWindowTitle(title: String) extends Signal
case class OpenDocument(path: String) extends Signal
case class DocumentOpened(doc: Document) extends Signal
case class Shutdown() extends Signal
case class RegisterStylesheet(path: String) extends Signal