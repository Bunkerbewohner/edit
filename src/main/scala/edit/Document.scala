package edit

import collection.mutable.ArrayBuffer

class Document {

  val lines = ArrayBuffer[String]()



}

class Caret {
  private var _x: Float = 0
  private var _y: Float = 0
}
