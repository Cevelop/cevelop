package com.cevelop.includator.helpers;

import java.net.URI;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;


@SuppressWarnings("restriction")
public class DocumentLocation {

    private final IASTFileLocation astLocation;

    public DocumentLocation(IASTFileLocation astLocation) {
        this.astLocation = astLocation;
    }

    public String getDocumentLocationString() {

        final URI uri = FileHelper.stringToUri(astLocation.getFileName());
        final IDocument document = FileHelper.getDocument(uri);

        if (document == null) {
            return getFallbackLocationString();
        }

        int startLineNumber = astLocation.getStartingLineNumber();
        int endLineNumber = astLocation.getEndingLineNumber();

        if (startLineNumber == 0) {
            TextSelection ts = new TextSelection(document, astLocation.getNodeOffset(), astLocation.getNodeLength());
            startLineNumber = ts.getStartLine() + 1;
            endLineNumber = ts.getEndLine() + 1;
        }
        final int tabWidth = getTabWidth(uri);

        int startLineOffset;
        try {
            startLineOffset = getLineOffset(document, startLineNumber);
            final int locationStartOffset = astLocation.getNodeOffset();
            final int leadingStartTabs = countTabs(document, startLineOffset, locationStartOffset);
            int offsetInStartLine = locationStartOffset - startLineOffset + leadingStartTabs * (tabWidth - 1) + 1;

            final int endLineOffset = getLineOffset(document, endLineNumber);
            final int locationEndOffset = locationStartOffset + astLocation.getNodeLength();
            final int leadingEndTabs = countTabs(document, endLineOffset, locationEndOffset);
            int offsetInEndLine = locationEndOffset - endLineOffset + leadingEndTabs * (tabWidth - 1) + 1;

            return "[" + startLineNumber + ":" + offsetInStartLine + "," + endLineNumber + ":" + offsetInEndLine + "]";
        } catch (BadLocationException e) {
            return getFallbackLocationString();
        }
    }

    private String getFallbackLocationString() {
        return "[" + astLocation.getNodeOffset() + "," + (astLocation.getNodeOffset() + astLocation.getNodeLength()) + "]";
    }

    private static int getLineOffset(IDocument document, int lineNumber) throws BadLocationException {
        return document.getLineOffset(lineNumber - 1);
    }

    private static int getTabWidth(final URI uri) {
        IFile file = FileHelper.getIFile(uri);
        ICProject cproject = CoreModel.getDefault().create(file.getProject());
        int tabWidth = CodeFormatterUtil.getTabWidth(cproject);
        return tabWidth;
    }

    private static int countTabs(IDocument document, int startLineOffset, int locationStartOffset) throws BadLocationException {
        int leadingStartTabs = 0;
        for (int i = startLineOffset; i < locationStartOffset; i++) {
            switch (document.getChar(i)) {
            case '\t':
                leadingStartTabs++;
                break;
            case '\r':
            case '\n':
                leadingStartTabs = 0;
            }
        }
        return leadingStartTabs;
    }
}
