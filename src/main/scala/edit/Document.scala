package edit

import collection.mutable.ArrayBuffer

class Document {

  protected var _x = 0
  protected var _y = 0

  val lines = ArrayBuffer[StringBuilder](new StringBuilder(""))

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
    _x = math.min(lines(y).length, i)
    assert(_x >= 0)
  }

  def y = _y
  def y_=(i: Int) {
    // can't move caret beyond the last line
    _y = math.min(lines.length - 1, i)
    assert(_y >= 0)
  }

  /**
   * Inserts a string at the current caret position.
   * @param str Any string
   */
  def insert(str: String) {
    str.foreach(c => insert(c))
  }

  /**
   * Inserts a single character into the document
   * @param char any character
   */
  def insert(char: Char) {
    if (char == '\n') {
      linebreak()
    } else {
      lines(y).insert(x, char)
      x += 1
    }
  }

  /**
   * Deletes the character at the current caret position. If the caret is at the end of
   * a line, the line break will be removed and the next line will be joined with the current one.
   */
  def deleteChar() {
    if (x < lines(y).length) {
      // just delete the current char
      lines(y).deleteCharAt(x)
    } else if (x == lines(y).length && y < lines.length) {
      // delete the implicit line break by joining the current and next line
      lines(y).append(lines.remove(y + 1))
    }
  }

  /**
   * Deletes the current line from the document.
   */
  def deleteLine() {
    if (lines.length > 1) {
      lines.remove(y)
      y -= 1
    } else {
      lines(0).clear()
    }
  }

  /**
   * Inserts a line break at the current caret position. If the caret is not at the end of the current line the
   * rest content of the line will be moved to the newly created next line.
   */
  protected def linebreak() {
    val rest = lines(y).substring(x)
    lines(y).delete(x, lines(y).length)
    lines.insert(y + 1, new StringBuilder())
    x = 0; y += 1
    insert(rest)
  }

}