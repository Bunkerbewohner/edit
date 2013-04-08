package edit.view

import javafx.scene.layout.{HBox, VBox}
import javafx.scene.text.{TextBuilder, Font, Text}
import edit.{Event, Document, Events}
import javafx.scene.input.MouseEvent
import javafx.application.Platform
import edit.util.JavaFX

class Textpane(doc: Document) extends VBox {

  protected val _lines = collection.mutable.ArrayBuffer[Line]()
  protected var _currentLine = Option.empty[Line]
  protected var _charWidth = 0.0
  protected var _charHeight = 0.0

  protected var _fontFamily: String = "Inconsolata"
  protected var _fontSize: Int = 14

  protected var _syntaxHighlighter: Option[SyntaxHighlighter] = None

  def fontFamily = _fontFamily
  def fontFamily_=(family: String) {
    _fontFamily = family
    computeCharSize()
  }

  def fontSize = _fontSize
  def fontSize_=(size: Int) {
    _fontSize = size
    computeCharSize()
  }

  def charWidth = _charWidth
  def textHeight = _charHeight
  def numLines = _lines.length

  def setSyntaxHighlighter(sh: SyntaxHighlighter) {
    _syntaxHighlighter.foreach(h => getStyleClass.remove(h.getStyleClass))
    _syntaxHighlighter = Some(sh)
    getStyleClass.add(sh.getStyleClass)
    JavaFX.applicationThread(rebuild)
  }

  def rebuild() {
    _lines.clear()

    val ls = doc.lines.zipWithIndex.map(p => {
      val line = new Line(p._1.toString(), p._2, _syntaxHighlighter)
      line.setOnMouseClicked(Events.eventHandler(onLineClicked))
      line
    })

    _lines.appendAll(ls)
    layout()

    _currentLine.foreach(l => l.getStyleClass.remove("current"))
    if (_lines.length > doc.y) {
      _currentLine = Some(_lines(doc.y))
      _currentLine.get.getStyleClass.add("current")
    }

    getChildren.clear()
    _lines.foreach(l => getChildren.add(l))
  }

  protected def getDefaultStyles = {
    s"-fx-font-family: '$fontFamily'; -fx-font-size: ${fontSize}px; -fx-font-smoothing-type: lcd; "
  }

  protected def computeCharSize() {
    val text = new Text()
    text.setFont(Font.font(fontFamily, fontSize))
    text.setStyle(getDefaultStyles)
    text.setText("A")
    text.snapshot(null, null)
    _charHeight = text.getLayoutBounds.getHeight
    _charWidth = text.getLayoutBounds.getWidth
  }

  def onLineClicked(e: MouseEvent) {
    val line = e.getSource.asInstanceOf[Line]

    doc.y = line.y
    doc.x = (e.getX / _charWidth).asInstanceOf[Int]

    _currentLine.foreach(l => l.getStyleClass.remove("current"))
    _currentLine = Some(line)

    line.getStyleClass.add("current")
    rebuild()
  }

  def setup() {
    computeCharSize()
    getStyleClass.add("textpane")
    getStyleClass.add("editor")
    _syntaxHighlighter.foreach(h => getStyleClass.add(h.getStyleClass))
    rebuild()
  }

  def init() {
    computeCharSize()
    rebuild()

    doc.caretChanged += (Unit => rebuild())
  }

  setup()
}

class Line(var text: String, var y: Int, val syntaxHighlighter: Option[SyntaxHighlighter] = None) extends HBox {

  val texts = collection.mutable.ArrayBuffer[Text]()

  protected def rebuild() {
    texts.clear()

    if (!syntaxHighlighter.isDefined) {
      texts.append(createText(text))

    } else {
      val highlighter = syntaxHighlighter.get
      val fragments = highlighter.annotateLineCached(y, text).map(f => {
        val t = createText(f.text)
        f.styleClasses.split(" ").foreach(c => t.getStyleClass.add(c))
        t
      })

      texts.appendAll(fragments)
    }

    texts.foreach(t => getChildren.add(t))
  }

  protected def setup() {
    getStyleClass.add("line")
    rebuild()
  }

  protected def createText(content: String) = {
    val text = new Text(content)
    text.getStyleClass.add("text")
    val fontFamily = "Inconsolata"
    val fontSize = 14
    text.setStyle(s"-fx-font-family: '$fontFamily'; -fx-font-size: ${fontSize}px; -fx-font-smoothing-type: lcd; ")
    text
  }

  setup()
}
