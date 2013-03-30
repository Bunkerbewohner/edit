package edit.interfaces;

public interface Line {
    String getContent();
    void setContent(String content);
    void insertContent(String content, int x);
    int getNumber();
}
