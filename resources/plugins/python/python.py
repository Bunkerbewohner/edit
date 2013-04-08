# This plugins adds rudimentary Python support with syntax highlighting

from edit.view import *
from edit import *
from org.python.modules import re

class PythonTokenizer:

    tokens = []
    s = 0
    i = -1

    def __init__(self, text):
        self.text = text

    def next(self):
        """ Returns the next token """

        def isSpace(str):
            return re.match("\s", str)

        self.i += 1
        if self.i >= len(self.text):
            return ""

        token = self.text[i]
def

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
        inString = 0

        while i < len(text):

            if inString == 0 and (text[i] == '"' or text[i] == "'"):
                inString = 1
                tokens[t] += text[i]
                i += 1
                continue
            elif inString == 1 and text[i] != '"' and text[i] != "'":
                inString = 0

            if text[i] == "#":
                if len(tokens[t]) == 0:
                    tokens[t] += text[i:]
                    break
                else:
                    tokens.append(text[i:])
                    break

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

        # early exit for commented lines
        if (re.match("^\s*#", text)):
            return [AnnotatedFragment(text, "comment")]

        # other lines have to be tokenized first
        tokens = self.tokenize(text)
        print tokens
        fragments = []

        for t in tokens:
            classes = []

            if self.keywordMap.has_key(t):
                classes.append("keyword")

            if t.startswith("#"):
                classes.append("comment")

            fragments.append(AnnotatedFragment(t, " ".join(classes)))

        return fragments

class PythonSyntaxHighlighterFactory(SyntaxHighlighterFactory):

    def createSyntaxHighlighter(self, doc):
        return PythonSyntaxHighlighter(doc)

edit << RegisterSyntaxHighlighter("py", PythonSyntaxHighlighterFactory())
edit << RegisterStylesheet(pluginDir + "style.css")
edit << UseSyntaxHighlighting("py")

print "Plugin 'python.py' was loaded"
