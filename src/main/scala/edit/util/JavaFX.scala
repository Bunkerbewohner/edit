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

}
