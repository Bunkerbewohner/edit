# This plugins adds rudimentary Python support with syntax highlighting

from edit.view import *
from edit import *

class PythonSyntaxHighlighter(SyntaxHighlighter):
    def __init__(self, doc):
        SyntaxHighlighter.__init__(self, doc)

    def getStylesheet():
        return "style.css"

    def annotateLine(lineNumber, text):
        return [AnnotatedFragment(text, "python")]

class PythonSyntaxHighlighterFactory(SyntaxHighlighterFactory):

    def createSyntaxHighlighter(doc):
        return PythonSyntaxHighlighter(doc)

edit << RegisterSyntaxHighlighter("py", PythonSyntaxHighlighterFactory())
edit << RegisterStylesheet(pluginDir + "style.css")
