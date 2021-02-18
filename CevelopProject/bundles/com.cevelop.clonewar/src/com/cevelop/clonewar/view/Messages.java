package com.cevelop.clonewar.view;

/**
 * Messages for the clonewarlication.
 *
 * @author ythrier(at)hsr.ch
 */
public final class Messages {

    /**
     * The title of the error box if the refactoring startup has an error.
     */
    public static final String STARTUP_ERROR_MSG = "Template Extraction Problem";

    /**
     * The name of the extraction page.
     */
    public static final String EXTRACTION_PAGE_NAME = "Select Extraction";

    /**
     * Starting message for the clonewar runner.
     */
    public static final String STARTUP_RUNNER_MSG = "Starting CloneWar...";

    /**
     * Message displayed on the extraction page on top after the title.
     */
    public static final String EXTRACTION_PAGE_MAIN_MSG = "Select the types for extraction.";

    /**
     * Info message on how to use the refactoring. Displayed on the extraction
     * page.
     */
    public static final String EXTRACTION_PAGE_INFO_MSG = "Only existing types can be replaced. The ordering of\n" +
                                                          "the template parameters is determined by the table ordering.";

    /**
     * Warning message on type conversion. Replace ?1 and ?2 with template and
     * variable.
     */
    public static final String TYPE_CONVERSION_WARNING = "Possible conversion problem for '?1' at variable '?2'";

    /**
     * Ordering title.
     */
    public static final String ORDERING_TITLE = "Ordering Preview:";

    /**
     * Text of the add button.
     */
    static final String ADD_TEXT = "Add";

    /**
     * Invalid default template type order.
     */
    public static final String INVALID_DEFAULTING = "Invalid default template ordering";

    /**
     * Text of the remove button.
     */
    public static final String REM_TEXT = "Remove";

    /**
     * Text of the up button.
     */
    public static final String UP_TEXT = "Up";

    /**
     * Text of the down button.
     */
    public static final String DOWN_TEXT = "Down";

    /**
     * Headings of the selection table of the function transformation.
     */
    public static final String[] FUNCTION_SELECTION_TABLE_HEADINGS = { "Name of type", "Type before extraction", "Type after extraction" };

    /**
     * Headings of the selection table of the type transformation.
     */
    public static final String[] TYPE_SELECTION_TABLE_HEADINGS = new String[] { "Defaulting", FUNCTION_SELECTION_TABLE_HEADINGS[0],
                                                                                FUNCTION_SELECTION_TABLE_HEADINGS[1],
                                                                                FUNCTION_SELECTION_TABLE_HEADINGS[2] };

    /**
     * Dialog title to select types for extraction.
     */
    public static final String TYPE_SELECTION_DIALOG_TITLE = "Select a String (* = any string, ? = any char):";

    /**
     * Dialog text to select types for extraction.
     */
    public static final String TYPE_SELECTION_DIALOG_TEXT = "Type selection";
}
