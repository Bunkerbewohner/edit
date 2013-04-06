package edit.util

import javafx.application.Platform

object JavaFX {

  def runLater(f: () => Unit) {
    Platform.runLater(new Runnable() {
      def run() {
        f()
      }
    })
  }

  def applicationThread(f: () => Unit) {
    if (!Platform.isFxApplicationThread) runLater(f)
    else f()
  }

}
