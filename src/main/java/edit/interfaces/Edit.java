package edit.interfaces;

public interface Edit {

    void setWindowTitle(String title);
    void openFile(String path);

    Document getDocument();

}
