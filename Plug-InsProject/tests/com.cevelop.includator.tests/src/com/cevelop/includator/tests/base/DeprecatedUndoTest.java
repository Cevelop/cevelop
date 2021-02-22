package com.cevelop.includator.tests.base;

public class DeprecatedUndoTest extends IncludatorTest {

    /**
     * This method exists due to the fact that undo markers have been removed from Includator features. Running this method will "emulate" the
     * applying of an old undo-marker so tests still run.
     * 
     * @param includeInsertOffset
     * The insert offset
     * @param includeString
     * The include as {@link String}
     * @param fwdStartOffset
     * The forward offset
     * @param fwdLength
     * The forward length
     */
    protected void applyFwdUndoMarker(int includeInsertOffset, String includeString, int fwdStartOffset, int fwdLength) throws Exception {
        deleteFromActiveDocument(fwdStartOffset, fwdLength);
        insertTextIntoActiveDocument(includeString + NL, includeInsertOffset);
    }

    protected void applyAddIncludeUndoMarker(int offset, int length) throws Exception {
        deleteFromActiveDocument(offset, length);
    }
}
