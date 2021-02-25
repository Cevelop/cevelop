package com.cevelop.macronator.refactoring;

import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.text.ICCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.cevelop.macronator.MacronatorPlugin;
import com.cevelop.macronator.common.LocalExpansion;


public class ExpandMacroProposal implements ICCompletionProposal {

    private final IASTNodeLocation               location;
    private final IASTPreprocessorMacroExpansion macroExpansion;

    public ExpandMacroProposal(IASTPreprocessorMacroExpansion macroExpansion) {
        this.macroExpansion = macroExpansion;
        this.location = macroExpansion.getFileLocation();
    }

    @Override
    public void apply(IDocument document) {
        try {
            String replacement = new LocalExpansion(macroExpansion).getExpansion();
            document.replace(location.getNodeOffset(), location.getNodeLength(), replacement);
        } catch (Exception e) {
            MacronatorPlugin.log(e, "Could not apply quickassist");
        }
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return "Replaces this macro reference with its expansion";
    }

    @Override
    public String getDisplayString() {
        return "Expand locally / remove reference";
    }

    @Override
    public Image getImage() {
        return CDTSharedImages.getImage(CDTSharedImages.IMG_OBJS_MACRO);
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
        return "com.cevelop.macronator.plugin.assist.LocalExpansion";
    }
}
