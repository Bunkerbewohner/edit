package edit

import javafx.event.EventHandler
import javafx.scene.input.KeyEvent

class EventHandlerFunc[T<:javafx.event.Event](f: T => Unit) extends EventHandler[T] {
  def handle(p1: T) {
    f(p1)
  }
}

object Events {
  implicit def eventHandler[T<:javafx.event.Event](func: T => Unit) = new EventHandlerFunc[T](func)
}

class Event[T] {

  type EventHandler = (T) => Unit

  val listeners = new scala.collection.mutable.ListBuffer[EventHandler]()

  def +=(h: EventHandler) {
    listeners.append(h)
  }

  def -=(h: EventHandler) {
    listeners.remove(listeners.indexOf(h))
  }

  def apply(arg: T) {
    listeners.foreach(l => l(arg))
  }
}
