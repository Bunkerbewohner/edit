package edit

import collection.mutable.ArrayBuffer

class Document {

  var tabSize = 4
  var tabReplace = " " * tabSize

  val contentChanged = new Event[Document]()
  val caretChanged = new Event[Unit]()

  // caret position
  protected var _x = 0
  protected var _y = 0

  /**
   * The lines of the document
   */
  val lines = ArrayBuffer[StringBuilder](new StringBuilder(""))

  def currentLine = lines(y)

  /**
   * Returns the complete text separated by the specified line break delimiter.
   * @return Complete content as one string
   */
  def text(linebreak: String = "\n") = {
    val sb = new StringBuilder()
    var i = 0
    while (i < lines.length) {
      if (i > 0) sb.append(linebreak)
      sb.append(lines(i))
      i += 1
    }
    sb.toString()
  }

  def x = _x
  def x_=(i: Int) {
    // don't move caret beyond end of the line
    val temp = math.max(0, math.min(lines(y).length, i))
    if (temp != _x) {
      _x = temp
      caretChanged((_x, _y))
    }
  }

  def y = _y
  def y_=(i: Int) {
    // can't move caret beyond the last line
    val temp = math.max(0, math.min(lines.length - 1, i))
    if (temp != _y) {
      _y = temp
      x = math.min(lines(y).length, x)
      caretChanged()
    }
  }

  def insert(str: String) {
    if (str.length == 0) return
    val strLines = str.split('\n')
    var i = 0

    while (i < strLines.length) {
      if (i > 0) linebreak()

      if (strLines(i).length > 0) {
        val text = prepare(strLines(i))

        if (x < lines(y).length) {
          lines(y).insert(x, text)
        } else {
          lines(y).append(text)
        }
        _x += text.length
      }

      i += 1
    }

    contentChanged(this)
    x = strLines(i - 1).length
  }

  def prepare(str: String) = {
    str.replace("\t", tabReplace)
  }

  /**
   * Inserts a single character into the document
   * @param char any character
   */
  def insert(char: Char) {
    if (char == '\n') {
      linebreak()
    } else if (char == '\t') {
      insert(tabReplace)
    } else {
      lines(y).insert(x, char)
      x += 1
    }
    contentChanged(this)
  }

  /**
   * Deletes the character at the current caret position. If the caret is at the end of
   * a line, the line break will be removed and the next line will be joined with the current one.
   */
  def deleteChar() {
    if (x < lines(y).length) {
      // just delete the current char
      lines(y).deleteCharAt(x)
      contentChanged(this)
    } else if (x == lines(y).length && y < lines.length - 1) {
      // delete the implicit line break by joining the current and next line
      lines(y).append(lines.remove(y + 1))
      contentChanged(this)
    }
  }

  /**
   * Deletes the current line from the document.
   */
  def deleteLine() {
    if (lines.length > 1) {
      lines.remove(y)
      y -= 1
      contentChanged(this)
    } else {
      lines(0).clear()
      contentChanged(this)
    }
  }

  /**
   * Inserts a line break at the current caret position. If the caret is not at the end of the current line the
   * rest content of the line will be moved to the newly created next line.
   */
  protected def linebreak() {
    val rest = lines(_y).substring(x)
    val restEmpty = rest.isEmpty

    if (!restEmpty) lines(_y).delete(x, lines(_y).length)

    lines.insert(_y + 1, new StringBuilder())
    _x = 0; _y += 1

    if (!restEmpty) insert(rest)
    _x = 0
  }

}