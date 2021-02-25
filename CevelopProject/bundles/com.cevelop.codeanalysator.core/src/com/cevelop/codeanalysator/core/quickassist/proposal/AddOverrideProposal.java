package com.cevelop.codeanalysator.core.quickassist.proposal;

import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.cevelop.codeanalysator.core.quickassist.handler.OverriderRefactoringHandler;
import com.cevelop.codeanalysator.core.quickassist.runnable.OverrideProposoalRunnable;


public class AddOverrideProposal implements ICCompletionProposal {

    private OverrideProposoalRunnable runnable;
    private String                    displayString = "Apply overrider";
    private final IEditorPart         editor;

    public AddOverrideProposal(OverrideProposoalRunnable runnable, IEditorPart editor) {
        this.runnable = runnable;
        this.displayString = runnable.displayString;
        this.editor = editor;
    }

    @Override
    public void apply(IDocument document) {
        new OverriderRefactoringHandler(runnable).execute(editor);
    }

    @Override
    public Point getSelection(IDocument document) {

        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayString() {
        return displayString;
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_SHLIB);
    }

    @Override
    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRelevance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getIdString() {
        // TODO Auto-generated method stub
        return "com.cevelop.codeanalysator.core.plugin.assist.Overrider";
    }

}
