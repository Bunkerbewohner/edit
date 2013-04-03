package edit.interfaces;

public interface Line {
    String getContent();
    void setContent(String content);
    void insertContent(String content, int x);
    void appendContent(String content);
    int getNumber();

    LineTag[] getTags();
    void addTag(LineTag tag);
}
