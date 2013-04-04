package edit.interfaces;

import edit.Signal;

public interface Edit {

    void setWindowTitle(String title);
    void openFile(String path);

    Document getDocument();

    void __lshift__(Signal signal);

}
