package edit.interfaces;

import edit.Signal;
import edit.view.SyntaxHighlighter;

public interface Edit {

    void setWindowTitle(String title);
    void openFile(String path);

    Document getDocument();

    void __lshift__(Signal signal);

}
