package edit

import collection.mutable.ArrayBuffer
import java.util.concurrent.{TimeUnit, PriorityBlockingQueue, BlockingQueue}

object Signals {

  val receptors = collection.mutable.Map[Class[_], ArrayBuffer[Receptor]]()

  def addReceptor(r: Receptor, signalType: Class[_ <: Signal]) {
    receptors.getOrElseUpdate(signalType, ArrayBuffer[Receptor]()).append(r)
  }

  def addReceptor(r: Receptor, signalTypes: List[Class[_ <: Signal]]) {
    signalTypes.foreach(t => addReceptor(r, t))
  }

  def send(signal: Signal) {
    if (receptors.contains(signal.getClass)) {
      receptors(signal.getClass).foreach(r => r << signal)
    }
  }

  def <<(signal: Signal) {
    send(signal)
  }

}

trait Receptor {
  private val signals = new PriorityBlockingQueue[Signal]()
  private var alive = true

  def reactLoop() {
    while (alive) {
      react()
    }
  }

  def react() {
    val signal = signals.poll(1, TimeUnit.SECONDS)
    if (signal == null) return

    signal match {
      case KillReceptor() => {
        alive = false
      }
      case s: Signal => receive(s)
    }
  }

  def send(signal: Signal) {
    signals.add(signal)
  }

  def <<(signal: Signal) {
    send(signal)
  }

  protected def receive(signal: Signal)

  Signals.addReceptor(this, classOf[KillReceptor])
}

trait Signal extends Comparable[Signal] {

  def signalPriority: Integer = 0

  def compareTo(s: Signal) = {
    s.signalPriority - this.signalPriority
  }
}

case class KillReceptor() extends Signal
