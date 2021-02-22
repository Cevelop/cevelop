package com.cevelop.codeanalysator.core.quickassist.proposal;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.cevelop.codeanalysator.core.quickassist.handler.StructClassSwitcherRefactoringHandler;


public class StructClassSwitcherProposal implements ICCompletionProposal {

    private final ICPPASTCompositeTypeSpecifier compositeTypeSpecifier;
    private final IEditorPart                   editor;

    public StructClassSwitcherProposal(ICPPASTCompositeTypeSpecifier compositeTypeSpecifier, IEditorPart editor) {
        this.compositeTypeSpecifier = compositeTypeSpecifier;
        this.editor = editor;
    }

    @Override
    public void apply(IDocument document) {
        new StructClassSwitcherRefactoringHandler(compositeTypeSpecifier).execute(editor);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        if (compositeTypeSpecifier.getKey() == IASTCompositeTypeSpecifier.k_struct) {
            return "Switch struct to class";
        } else {
            return "Switch class to struct";
        }
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_SHLIB);
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public int getRelevance() {
        return 0;
    }

    @Override
    public String getIdString() {
        return "com.cevelop.codeanalysator.core.quickassist.StructClassSwitcher";
    }

}
