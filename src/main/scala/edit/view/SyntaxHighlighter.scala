package edit.view

import edit.interfaces.Document
import edit.{Receptor, Signal, Signals}
import collection.mutable.ArrayBuffer

abstract class SyntaxHighlighter(val doc: Document) {

  // TODO: Remove unused cache entries at some point
  val cache = collection.mutable.Map[String, Array[AnnotatedFragment]]()

  def getStyleClass: String

  def annotateLine(lineNumber: Int, text: String): Array[AnnotatedFragment]

  def annotateLineCached(lineNumber: Int, text: String): Array[AnnotatedFragment] = {
    if (cache.contains(text)) cache(text)
    else updateCache(lineNumber, text)
  }

  def updateCache(lineNumber: Int, text: String) = {
    val fragments = annotateLine(lineNumber, text)
    cache.put(text, fragments)
    fragments
  }
}

trait SyntaxHighlighterFactory {
  def createSyntaxHighlighter(doc: Document): SyntaxHighlighter
}

case class AnnotatedFragment(text: String, styleClasses: String)

object SyntaxHighlighters extends Receptor {
  protected var _highlighters = Map[String, SyntaxHighlighterFactory]()

  def highlighters = _highlighters

  def registerSyntaxHighlighter(fileExtension: String, highlighter: SyntaxHighlighterFactory) {
    _highlighters = _highlighters + (fileExtension -> highlighter)
    Signals << SyntaxHighlighterRegistered(fileExtension, highlighter)
  }

  protected def receive(signal: Signal) {
    signal match {
      case RegisterSyntaxHighlighter(ext, h) => registerSyntaxHighlighter(ext, h)
    }
  }

  def init() {
    Signals.addReceptor(this, classOf[RegisterSyntaxHighlighter])
    reactLoopAsync()
  }
}

case class RegisterSyntaxHighlighter(fileExt: String, highlighter: SyntaxHighlighterFactory) extends Signal
case class SyntaxHighlighterRegistered(fileExt: String, highlighter: SyntaxHighlighterFactory) extends Signal