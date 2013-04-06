package edit.view

import edit.interfaces.Document
import edit.{Receptor, Signal, Signals}

abstract class SyntaxHighlighter(val doc: Document) {

  def getStyleClass: String

  def annotateLine(lineNumber: Int, text: String): Array[AnnotatedFragment]

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