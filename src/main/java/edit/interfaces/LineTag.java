package edit.interfaces;

/**
 * Additional information about a line in a document used for syntax highlighting and other augmentations.
 */
public interface LineTag {

    /**
     * Gets the line that this tag is associated with
     * @return
     */
    int getLineNumber();

    /**
     * Return a space separated list of style classes that should be applied to this line.
     * @return
     */
    String getStyleClasses();

    /**
     * Return the start of the range in this line that this tag applies to
     * @return
     */
    int getRangeStart();

    /**
     * Return the end of the range in this line that htis tag applies to
     * @return
     */
    int getRangeEnd();

    /**
     * Returns a generic tag (by default null)
     * @return
     */
    Object getTag();

}
