package edit

import javafx.scene.layout.StackPane
import javafx.scene.input.{KeyCode, KeyEvent}

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
    if (!e.getCharacter.trim().isEmpty) {
      val char = e.getCharacter()(0)
      doc.insert(char)
    } else if (e.getCharacter.codePointAt(0) == 32) {
      doc.insert(" ")
    }
  }

  def onKeyReleased(e: KeyEvent) {
    e.getCode match {
      case _ => // do nothing
    }
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
      case KeyCode.ENTER => doc.insert("\n")
      case _ => // do nothing
    }
  }

  setOnKeyTyped(Events.eventHandler(onKeyTyped))
  setOnKeyReleased(Events.eventHandler(onKeyReleased))
  setOnKeyPressed(Events.eventHandler(onKeyPressed))
  getChildren.add(view)
}
