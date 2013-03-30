package edit.interfaces;

public interface Document {

    Line getCurrentLine();
    Line[] getLines();
    int getLineCount();

    /**
     * Add a line tag
     * @param tag
     */
    void addLineTag(LineTag tag);

    /**
     * Gets the existing tags for a given line
     * @param lineNumber line number
     * @return
     */
    LineTag[] getLineTags(int lineNumber);

}
