package edit

import javafx.application._
import javafx.stage.Stage

class Edit extends Application {
  def start(stage: Stage) {
    stage.setTitle("edit")
    stage.setWidth(800)
    stage.setHeight(600)
    stage.show()
  }
}

object Edit {
  def main(args: Array[String]) {
    Application.launch(classOf[Edit], args: _*)
  }
}