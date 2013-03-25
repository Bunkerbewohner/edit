package edit

import javafx.scene.layout.{Pane, VBox, StackPane}
import javafx.scene.text.{Font, Text}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Paint
import javafx.animation.FadeTransition
import javafx.util.Duration
import javafx.scene.input.MouseEvent

class DocumentView(document: Document) extends StackPane {

  class Line(var text: String, var y: Int) extends VBox {

    val textControl = new Text(text)
    textControl.setStyle(getDefaultStyles)
    textControl.setFont(Font.font(fontFamily, fontSize))
    getChildren.add(textControl)
    setOnMouseClicked(Events.eventHandler(onClick))

    def onClick(e: MouseEvent) {
      doc.y = y
      doc.x = (e.getX / charWidth).asInstanceOf[Int]
    }
  }

  protected var doc: Document = document
  protected val textpane = new VBox()
  protected val backgroundPane = new Pane()
  protected val caret = createCaret()

  protected var charWidth = 0.0
  protected var charHeight = 0.0

  protected var _fontFamily = "Inconsolata"
  protected var _fontSize = 14

  protected val fadeIn = new FadeTransition(Duration.millis(100))
  protected val fadeOut = new FadeTransition(Duration.millis(100))

  def fontFamily = _fontFamily
  def fontSize = _fontSize

  def getDefaultStyles = {
    s"-fx-font-family: '$fontFamily'; -fx-font-size: ${fontSize}px; -fx-font-smoothing-type: lcd; "
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

  def updateCaret() {
    caret.setX(doc.x * charWidth)

    if (doc.y < textpane.getChildren.size()) {
      val curText = textpane.getChildren.get(doc.y)
      val height = curText.getBoundsInParent.getMinY
      caret.setY(height)
    } else {
      caret.setY(doc.y * charHeight)
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

    layout()
    updateCaret()
  }

  def setup() {
    fadeIn.setFromValue(0)
    fadeIn.setToValue(1)
    fadeOut.setFromValue(1)
    fadeOut.setToValue(0)

    doc.contentChanged += (doc => rebuild())
    doc.caretChanged += (Unit => updateCaret())

    backgroundPane.getChildren.add(caret)
    getChildren.add(backgroundPane)

    setStyle(getDefaultStyles)
    getChildren.add(textpane)

    computeCharSize()

    rebuild()
  }

  setup()
}