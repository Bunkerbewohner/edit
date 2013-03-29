package edit

import input.KeyMap
import javafx.scene.layout.StackPane
import javafx.scene.input.{Clipboard, KeyCombination, KeyCode, KeyEvent}
import javafx.event.EventHandler
import java.io.File
import io.Source
import javafx.stage.FileChooser

class Editor(document: Document) extends StackPane {

  val doc = document
  val view = new DocumentView(doc)

  def home() {
    if (doc.x == 0 && doc.currentLine.length > 1) {
      var x = 0
      while (x < doc.currentLine.length) {
        if (!doc.currentLine.charAt(x).isWhitespace) {
          doc.x = x
          return
        }
        x += 1
      }
    } else {
      doc.x = 0
    }
  }

  def end(): Int = {
    doc.x = doc.currentLine.length
    doc.x
  }

  def backspace() {
    if (doc.x > 0) {
      doc.x -= 1; doc.deleteChar()
    } else if (doc.y > 0) {
      val line = doc.currentLine
      doc.deleteLine()
      val cursor = end()
      doc.insert(line.toString())
      doc.x = cursor
    }
  }

  def onKeyTyped(e: KeyEvent) {
    // check for printable chars (127 = DELETE)
    if (!e.getCharacter.trim().isEmpty && e.getCharacter.codePointAt(0) != 127) {
      val char = e.getCharacter()(0)
      doc.insert(char)
    } else if (e.getCharacter.codePointAt(0) == 32) {
      doc.insert(' ')
    }

    view.followCaret()
  }

  def onKeyReleased(e: KeyEvent) {
    KeyMap.handleKeyReleased(e)
  }

  def onKeyPressed(e: KeyEvent) {
    e.getCode match {
      case KeyCode.DELETE => doc.deleteChar()
      case KeyCode.BACK_SPACE => backspace()
      case KeyCode.LEFT => doc.x -= 1
      case KeyCode.RIGHT => doc.x += 1
      case KeyCode.UP => doc.y -= 1
      case KeyCode.DOWN => doc.y += 1
      case KeyCode.HOME => home()
      case KeyCode.END => end()
      case KeyCode.TAB => doc.insert("\t")
      case KeyCode.ENTER => doc.insert('\n')
      case _ => // do nothing
    }

    view.followCaret()
  }

  def registerActions() {
    val actions = scala.collection.Map[KeyCombination, (KeyEvent) => Unit](
      KeyCombination.keyCombination("Ctrl+V") -> (e => paste()),
      KeyCombination.keyCombination("Ctrl+O") -> (e => openFileDialog())
    )

    actions.foreach(a => KeyMap(a._1) = a._2)
  }

  def paste() {
    val clipboard = Clipboard.getSystemClipboard
    if (clipboard.hasString) {
      document.insert(clipboard.getString)
    }
  }

  def init() {
    view.init()
  }

  def load(file: File) {
    doc.clear()
    val source = Source.fromFile(file)
    doc.insert(source.mkString)
    source.close()
    doc.x = 0
    doc.y = 0

    Edit.stage.setTitle(s"edit '${file.getName}'")
  }

  def openFileDialog() {
    val chooser = new FileChooser()
    chooser.setTitle("Open file")
    val file = chooser.showOpenDialog(null)

    if (file != null && file.exists()) {
      load(file)
    }
  }

  setOnKeyTyped(Events.eventHandler(onKeyTyped))
  setOnKeyReleased(Events.eventHandler(onKeyReleased))
  setOnKeyPressed(Events.eventHandler(onKeyPressed))
  getChildren.add(view)
  registerActions()

  view.scrollpane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler[KeyEvent]() {
    def handle(e: KeyEvent) {

      if (e.getEventType == KeyEvent.KEY_PRESSED){
        // Consume Event before Bubbling Phase, -> otherwise Scrollpane scrolls
        if ( e.getCode == KeyCode.SPACE ){
          // but tell the editor
          onKeyPressed(e)
          e.consume()
        }
      }
    }
  })
}
