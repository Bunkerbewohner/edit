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
import javafx.scene.effect.BlendMode
import view.Textpane

class DocumentView(document: Document) extends StackPane {

  protected var doc: Document = document
  protected val textpane = new Textpane(doc)
  protected val backgroundPane = new Pane()
  protected val foregroundPane = new Pane()
  protected val scrollpane = new ScrollPane()
  protected val caret = createCaret()

  def getScrollpane = scrollpane
  def getTextpane = textpane

  var caretLayoutX = 0.0
  var caretLayoutY = 0.0

  /**
   * This offset should be retrieved from the stylesheet, which as of JavaFX 2.2 is NOT possible...
   * @return offset of the caret in pixels
   */
  def caretOffsetX = 5

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
    val xOffset = caretOffsetX - getScrollLeft
    val yOffset = (-1) * getScrollTop

    if (doc.y < textpane.numLines) {
      val curText = textpane.getChildren.get(doc.y)
      val height = curText.getBoundsInParent.getMinY
      caretLayoutX = xOffset + doc.x * textpane.charWidth
      caretLayoutY = yOffset + height
      setCaretPosition(caretLayoutX, caretLayoutY)
    }

    caret.setHeight(textpane.textHeight)
  }

  def getScrollTop = {
    scrollpane.getVvalue * (textpane.getHeight - scrollpane.getViewportBounds.getHeight)
  }

  def getScrollLeft = {
    scrollpane.getHvalue * (textpane.getWidth - scrollpane.getViewportBounds.getWidth)
  }

  def setScrollTop(pixel: Double) {
    val h = textpane.getHeight - scrollpane.getViewportBounds.getHeight
    if (h == 0) {
      scrollpane.setVvalue(0)
      return
    }
    val v = math.max(0.0, math.min(1.0, pixel / h))
    scrollpane.setVvalue(v)
  }

  def setScrollLeft(pixel: Double) {
    val w = textpane.getWidth - scrollpane.getViewportBounds.getWidth
    if (w == 0) {
      scrollpane.setHvalue(0)
      return
    }
    val v = math.max(0.0, math.min(1.0, pixel / w))
    scrollpane.setHvalue(v)
  }

  /**
   * Scrolls the view so that the caret is visible
   */
  def followCaret() {
    val charWidth = textpane.charWidth
    val charHeight = textpane.textHeight
    val caretX = caretLayoutX + getScrollLeft
    val caretY = caretLayoutY + getScrollTop
    val topDiff = getScrollTop - (caretY - charHeight)
    val bottomDiff = caretY + charHeight * 2 - (getScrollTop + scrollpane.getViewportBounds.getHeight)
    val leftDiff = getScrollLeft - (caretX - charWidth * 2)
    val rightDiff = caretX + charWidth * 3 - (getScrollLeft + scrollpane.getViewportBounds.getWidth)

    if (bottomDiff > 0 && bottomDiff > topDiff) {
      val h = math.ceil(bottomDiff / charHeight) * charHeight
      setScrollTop(getScrollTop + h)
    } else if (topDiff > 0) {
      val h = math.ceil(topDiff / charHeight) * charHeight
      setScrollTop(getScrollTop - h)
    }

    if (leftDiff > 0 && leftDiff > rightDiff) {
      val w = math.ceil(leftDiff / charWidth) * charWidth
      setScrollLeft(getScrollLeft - w)
    } else if (rightDiff > 0) {
      val w = math.ceil(rightDiff / charWidth) * charWidth
      setScrollLeft(getScrollLeft + w)
    }
  }

  def onLineClick(index: Int, text: Text) = {
    Events.eventHandler((e: MouseEvent) => {
      doc.y = index
    })
  }

  def highlightSelection(s: Selection) {
    val numLines = s.endRow - s.startRow
    val charWidth = textpane.charWidth
    val charHeight = textpane.textHeight

    if (numLines == 1) {
      val rect = new Rectangle()
      rect.getStyleClass.add("selection")
      rect.setWidth((s.endCol - s.startCol) * charWidth)
      rect.setHeight(charHeight)
      rect.setX(s.startCol * charWidth)

      foregroundPane.getChildren.add(rect)
    }

    // TODO: finish text selection
  }

  def highlightSelections() {
    doc.selections.foreach(highlightSelection(_))
  }

  def rebuild() {
    textpane.rebuild()
    textpane.layout()
    updateCaret()
  }

  def setup() {
    getStyleClass.add("document-view")
    setAlignment(Pos.TOP_LEFT)

    backgroundPane.getStyleClass.add("background-pane")
    getChildren.add(backgroundPane)

    scrollpane.setContent(textpane)
    scrollpane.getStyleClass.add("scrollpane")
    scrollpane.setFitToWidth(true)
    scrollpane.setFitToHeight(true)
    scrollpane.hvalueProperty().addListener(scrollListener)
    scrollpane.vvalueProperty().addListener(scrollListener)
    getChildren.add(scrollpane)

    caret.getStyleClass.add("caret")
    getChildren.add(caret)

    foregroundPane.getStyleClass.add("foregroundPane")
    foregroundPane.setFocusTraversable(false)
    foregroundPane.setMouseTransparent(true)
    getChildren.add(foregroundPane)
  }

  def init() {
    doc.x = 0
    doc.y = 0
    rebuild()
    doc.contentChanged += (doc => rebuild())
    doc.caretChanged += (Unit => rebuild())
    textpane.init()
  }

  val scrollListener = new InvalidationListener {
    def invalidated(p1: Observable) {
      updateCaret()
    }
  }

  setup()
}