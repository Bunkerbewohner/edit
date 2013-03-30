from edit import *

print "Plugin 'example.py' was loaded."

edit.setWindowTitle("edit")

line = edit.getDocument().getCurrentLine()

line.setContent("Hello, World!")
line
