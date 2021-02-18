package com.cevelop.namespactor.quickfix;

import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.cevelop.namespactor.handlers.ITDARefactoringMenuHandler;


public class ITDACompletionProposal implements ICCompletionProposal {

    private final IEditorPart editor;
    private int               fRelevance;

    public ITDACompletionProposal(IEditorPart editor) {
        this.editor = editor;
        this.setRelevance(10);
    }

    @Override
    public void apply(IDocument document) {
        new ITDARefactoringMenuHandler().execute(editor);
    }

    @Override
    public Point getSelection(IDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "Replace typedef or using alias with underlying type";
    }

    @Override
    public String getDisplayString() {
        return "Inline type alias";
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRelevance() {
        // TODO Auto-generated method stub
        return fRelevance;
    }

    @Override
    public String getIdString() {
        return getDisplayString();
    }

    public void setRelevance(int fRelevance) {
        this.fRelevance = fRelevance;
    }

}
