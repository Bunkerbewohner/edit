package edit.input

import javafx.scene.input.{KeyCombination, KeyCodeCombination, KeyCode, KeyEvent}
import javafx.event.EventHandler

object KeyMap {

  type Action = (KeyEvent) => Unit

  val actions = scala.collection.mutable.Map[KeyCombination, Action]()

  def apply(combo: KeyCombination) {
    actions.get(combo)
  }

  def update(combo: KeyCombination, action: Action) {
    actions.put(combo, action)
  }

  def handleKeyReleased(e: KeyEvent) {
    actions.filter(_._1.`match`(e)).map(_._2(e))
  }

  val keyReleasedHandler = new KeyReleaseHandler()
}

class KeyReleaseHandler extends EventHandler[KeyEvent] {
  def handle(keyEvent: KeyEvent) {
    KeyMap.handleKeyReleased(keyEvent)
  }
}

case class KeyComboSequence(combos: KeyCombination*)