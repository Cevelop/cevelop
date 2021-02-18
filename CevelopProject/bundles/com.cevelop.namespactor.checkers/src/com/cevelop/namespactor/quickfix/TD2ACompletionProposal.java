package com.cevelop.namespactor.quickfix;

import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.cevelop.namespactor.handlers.TD2ARefactoringMenuHandler;


public class TD2ACompletionProposal implements ICCompletionProposal {

    private final IEditorPart editor;
    private int               fRelevance;

    /**
     * @param relevance
     * the relevance to set
     */
    public void setRelevance(int relevance) {
        this.fRelevance = relevance;
    }

    public TD2ACompletionProposal(IEditorPart editor) {
        this.editor = editor;
        this.fRelevance = 9; // number higher than rename quick assist.
    }

    @Override
    public Point getSelection(IDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        // should provide a nicer image the icon-green-star.png would be nice
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_TYPEDEF_ALT);

        //return null;
    }

    @Override
    public String getDisplayString() {
        return "Change typedef to using alias";
    }

    @Override
    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "Use more obvious modern C++ syntax for type aliases";
    }

    @Override
    public void apply(IDocument document) {
        new TD2ARefactoringMenuHandler().execute(editor);
    }

    @Override
    public int getRelevance() {
        return fRelevance;
    }

    @Override
    public String getIdString() {
        return getDisplayString();
    }
}
