# This plugins adds rudimentary Python support with syntax highlighting

from edit.view import *
from edit import *

class PythonSyntaxHighlighter(SyntaxHighlighter):
    def __init__(self, doc):
        SyntaxHighlighter.__init__(self, doc)

    def getStyleClass(self):
        return "python"

    def annotateLine(self, lineNumber, text):
        return [AnnotatedFragment(text, "python")]

class PythonSyntaxHighlighterFactory(SyntaxHighlighterFactory):

    def createSyntaxHighlighter(self, doc):
        return PythonSyntaxHighlighter(doc)

edit << RegisterSyntaxHighlighter("py", PythonSyntaxHighlighterFactory())
edit << RegisterStylesheet(pluginDir + "style.css")
edit << UseSyntaxHighlighting("py")

print "Plugin 'python.py' was loaded"
