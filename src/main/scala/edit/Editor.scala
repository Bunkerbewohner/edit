package edit

import input.KeyMap
import javafx.scene.layout.StackPane
import javafx.scene.input.{Clipboard, KeyCombination, KeyCode, KeyEvent}
import javafx.event.EventHandler
import java.io.{FileWriter, File}
import io.Source
import javafx.stage.FileChooser
import view.SyntaxHighlighters

class Editor(document: Document) extends StackPane {

  val doc = document
  val view = new DocumentView(doc)
  var openedFile = Option.empty[File]

  val receptor = new Receptor {
    protected def receive(signal: Signal) {
      signal match {
        case UseSyntaxHighlighting(ext) => {
          val factory = SyntaxHighlighters.highlighters(ext)
          val highlighter = factory.createSyntaxHighlighter(doc)
          view.getTextpane.setSyntaxHighlighter(highlighter)
        }
      }
    }

    Signals.addReceptor(this, classOf[UseSyntaxHighlighting])
  }

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
      KeyCombination.keyCombination("Ctrl+O") -> (e => openFileDialog()),
      KeyCombination.keyCombination("Ctrl+S") -> (e => saveFileDialog()),
      KeyCombination.keyCombination("Ctrl+N") -> (e => newFile())
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
    receptor.reactLoopAsync()
  }

  def load(file: File) {
    doc.clear()
    val source = Source.fromFile(file)
    doc.insert(source.mkString)
    source.close()
    doc.x = 0
    doc.y = 0

    Signals << DocumentOpened(doc)
    Signals << SetWindowTitle(s"edit '${file.getName}'")
  }

  def newFile() {
    doc.clear()
    openedFile = None
    Edit.stage.setTitle("edit")
  }

  def openFileDialog() {
    val chooser = new FileChooser()
    chooser.setTitle("Open file")
    val file = chooser.showOpenDialog(null)

    if (file != null && file.exists()) {
      load(file)
      openedFile = Some(file)
    }
  }

  def saveFileDialog() {
    val file = openedFile.getOrElse({
      val chooser = new FileChooser
      chooser.setTitle("Save file")
      chooser.showSaveDialog(null)
    })

    if (file != null) {
      val writer = new FileWriter(file)
      try {
        writer.write(doc.text("\n"))
        Edit.stage.setTitle(s"edit '${file.getName}'")
      } finally {
        writer.close()
      }

      // TODO: notify about successful save operation
    }
  }

  setOnKeyTyped(Events.eventHandler(onKeyTyped))
  setOnKeyReleased(Events.eventHandler(onKeyReleased))
  setOnKeyPressed(Events.eventHandler(onKeyPressed))
  getChildren.add(view)
  registerActions()

  view.getScrollpane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler[KeyEvent]() {
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

case class UseSyntaxHighlighting(fileExt: String) extends Signal