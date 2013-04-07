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
        self.keywordMap = {}
        for keyword in self.keywords:
            self.keywordMap[keyword] = keyword

    def getStyleClass(self):
        return "python"

    def tokenize(self, text):
        i = 0
        t = 0
        tokens = [""]

        while i < len(text):
            space = re.match("\s", text[i])
            curTokenSpace = re.match("\s", tokens[t]) or len(tokens[t]) == 0

            if space:
                if curTokenSpace:
                    tokens[t] += text[i]
                else:
                    tokens.append(text[i])
                    t += 1
            elif curTokenSpace:
                if len(tokens[t]) == 0:
                    tokens[t] += text[i]
                else:
                    tokens.append(text[i])
                    t += 1
            else:
                tokens[t] += text[i]

            i += 1

        return tokens

    def annotateLine(self, lineNumber, text):

        if (text.startswith("#")):
            return [AnnotatedFragment(text, "comment")]

        tokens = self.tokenize(text)
        print tokens

        fragments = [AnnotatedFragment(text, "")]
        return fragments

class PythonSyntaxHighlighterFactory(SyntaxHighlighterFactory):

    def createSyntaxHighlighter(self, doc):
        return PythonSyntaxHighlighter(doc)

edit << RegisterSyntaxHighlighter("py", PythonSyntaxHighlighterFactory())
edit << RegisterStylesheet(pluginDir + "style.css")
edit << UseSyntaxHighlighting("py")

print "Plugin 'python.py' was loaded"
