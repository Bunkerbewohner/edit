package edit

import javafx.scene.layout.{Pane, VBox, StackPane}
import javafx.scene.text.{Font, Text}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Paint
import javafx.animation.FadeTransition
import javafx.util.Duration
import javafx.scene.input.{KeyEvent, DragEvent, ScrollEvent, MouseEvent}
import scala.collection.JavaConversions._
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.event.EventHandler
import javafx.beans.{Observable, InvalidationListener}

class DocumentView(document: Document) extends StackPane {

  class Line(var text: String, var y: Int) extends VBox {

    val textControl = new Text(text)
    textControl.setStyle(getDefaultStyles)
    textControl.setFont(Font.font(fontFamily, fontSize))
    setOnMouseClicked(Events.eventHandler(onClick))

    getStyleClass.add("line")
    textControl.getStyleClass.add("text")
    getChildren.add(textControl)
    textControl.setId("textline-" + y)

    def onClick(e: MouseEvent) {
      doc.y = y
      doc.x = (e.getX / charWidth).asInstanceOf[Int]

      if (currentLine != null) {
        currentLine.getStyleClass.remove("current")
      }

      getStyleClass.add("current")
      currentLine = this
    }
  }

  protected var doc: Document = document
  val textpane = new VBox()
  protected val backgroundPane = new Pane()
  protected val foregroundPane = new Pane()
  val scrollPane = new ScrollPane()
  protected val caret = createCaret()

  protected var charWidth = 0.0
  protected var charHeight = 0.0

  protected var _fontFamily = "Inconsolata"
  protected var _fontSize = 14

  protected val fadeIn = new FadeTransition(Duration.millis(100))
  protected val fadeOut = new FadeTransition(Duration.millis(100))

  protected var currentLine: Line = null
  protected var previousLineNr = -1

  /**
   * This offset should be retrieved from the stylesheet, which as of JavaFX 2.2 is NOT possible...
   * @return offset of the caret in pixels
   */
  def caretOffsetX = 5

  def fontFamily = _fontFamily
  def fontSize = _fontSize

  def getDefaultStyles = {
    s"-fx-font-family: '$fontFamily'; -fx-font-size: ${fontSize}px; -fx-font-smoothing-type: lcd; "
  }

  def setCurrentLine(lineNr: Int) = {
    if (currentLine != null) {
      currentLine.getStyleClass.remove("current")
    }

    if (textpane.getChildren.size() > doc.y) {
      currentLine = textpane.getChildren.get(doc.y).asInstanceOf[Line]
      currentLine.getStyleClass.add("current")
      true
    } else {
      false
    }
  }

  def computeCharSize() {
    val text = new Text()
    text.setFont(Font.font(fontFamily, fontSize))
    text.setStyle(getDefaultStyles)
    text.setText("a")
    text.snapshot(null, null)
    charHeight = text.getLayoutBounds.getHeight
    charWidth = text.getLayoutBounds.getWidth
    caret.setHeight(charHeight)
  }

  def createCaret() = {
    val caret = new Rectangle()
    caret.setWidth(1)
    caret.setFill(Paint.valueOf("#000000"))
    caret
  }

  def setCaretPosition(x: Double, y: Double) {
    caret.setStyle(s"-fx-translate-x: ${x}px; -fx-translate-y: ${y}px;")
  }

  def updateCaret() {
    val xOffset = caretOffsetX + scrollPane.getHvalue * (textpane.getWidth - scrollPane.getWidth)
    val yOffset = (-1) * scrollPane.getVvalue * (textpane.getHeight - scrollPane.getHeight)

    if (doc.y < textpane.getChildren.size()) {
      val curText = textpane.getChildren.get(doc.y)
      val height = curText.getBoundsInParent.getMinY
      setCaretPosition(xOffset + doc.x * charWidth, yOffset + height)
    }

    if (previousLineNr != doc.y) {
      if (setCurrentLine(doc.y)) {
        previousLineNr = doc.y
      }
    }
  }

  def onLineClick(index: Int, text: Text) = {
    Events.eventHandler((e: MouseEvent) => {
      doc.y = index
    })
  }

  def rebuild() {
    textpane.getChildren.clear()
    val lines = Range(0, doc.lines.length).map(i => new Line(doc.lines(i).toString(), i))
    lines.foreach(l => textpane.getChildren.add(l))

    textpane.layout()
    updateCaret()
    setCurrentLine(doc.y)
  }

  def setup() {
    fadeIn.setFromValue(0)
    fadeIn.setToValue(1)
    fadeOut.setFromValue(1)
    fadeOut.setToValue(0)

    this.getStyleClass.add("document-view")
    backgroundPane.getStyleClass.add("background-pane")
    getChildren.add(backgroundPane)

    setStyle(getDefaultStyles)
    setAlignment(Pos.TOP_LEFT)

    textpane.getStyleClass.add("textpane")
    scrollPane.setContent(textpane)
    scrollPane.getStyleClass.add("scrollpane")
    scrollPane.setFitToWidth(true)
    scrollPane.setFitToHeight(true)
    scrollPane.hvalueProperty().addListener(scrollListener)
    scrollPane.vvalueProperty().addListener(scrollListener)

    getChildren.add(scrollPane)
    textpane.getStyleClass.add("editor")

    getChildren.add(caret)
    caret.getStyleClass.add("caret")
    getStyleClass.add("foregroundPane")
  }

  def init() {
    doc.x = 0
    doc.y = 0
    computeCharSize()
    rebuild()
    doc.contentChanged += (doc => rebuild())
    doc.caretChanged += (Unit => updateCaret())
  }


  val scrollListener = new InvalidationListener {
    def invalidated(p1: Observable) {
      updateCaret()
    }
  }

  setup()
}