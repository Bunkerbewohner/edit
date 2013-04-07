# This plugins adds rudimentary Python support with syntax highlighting

from edit.view import *
from edit import *
from org.python.modules import re

class PythonSyntaxHighlighter(SyntaxHighlighter):

    keywords = ["and", "del", "for", "is", "raise", "assert", "elif", "from", "lambda", "return",
                "break", "else", "global", "not", "try", "class", "except", "if", "or", "while",
                "continue", "exec", "import", "pass", "yield", "def", "finally", "in", "print"]

    def __init__(self, doc):
        SyntaxHighlighter.__init__(self, doc)

    def getStyleClass(self):
        return "python"

    def annotateLine(self, lineNumber, text):
        fragments = []

        prefix = re.match("^\s+", text)
        if (prefix):
            prefix = prefix.group(0)
            fragments.append(AnnotatedFragment(prefix, ""))

        tokens = re.split(" ", text)

        for t in tokens:
            classes = ""
            for keyword in self.keywords:
                if re.match(keyword, t):
                    classes = "keyword"
                    break

            fragments.append(AnnotatedFragment(t, classes))
            fragments.append(AnnotatedFragment(" ", ""))

        return fragments

class PythonSyntaxHighlighterFactory(SyntaxHighlighterFactory):

    def createSyntaxHighlighter(self, doc):
        return PythonSyntaxHighlighter(doc)

edit << RegisterSyntaxHighlighter("py", PythonSyntaxHighlighterFactory())
edit << RegisterStylesheet(pluginDir + "style.css")
edit << UseSyntaxHighlighting("py")

print "Plugin 'python.py' was loaded"
