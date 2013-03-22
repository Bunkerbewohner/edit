name := "edit"

version := "0.1"

scalaVersion := "2.10.1"

//mainClass := Some("edit.EditApp")

javaHome := {
  var s = System.getenv("JAVA_HOME")
  if (s == null) throw new RuntimeException("JAVA_HOME not set")
  val dir = new File(s)
  if (!dir.exists) {
    throw new RuntimeException( "No JDK found - try setting 'JAVA_HOME'." )
  }
  Some(dir)
}

// Add JavaFX manually, since SBT can't handle it, despite it being part of the JDK7...
unmanagedJars in Compile <+= javaHome map { jh =>
  val dir: File = jh.getOrElse(null)
  val jfxJar = new File(dir, "jre/lib/jfxrt.jar")
  if (!jfxJar.exists) {
    throw new RuntimeException( "JavaFX not detected (needs Java runtime 7u06 or later): "+ jfxJar.getPath )
  }
  Attributed.blank(jfxJar)
}