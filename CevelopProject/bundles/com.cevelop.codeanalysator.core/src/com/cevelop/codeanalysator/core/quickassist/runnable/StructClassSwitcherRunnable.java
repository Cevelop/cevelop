package com.cevelop.codeanalysator.core.quickassist.runnable;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


@SuppressWarnings("restriction")
public class StructClassSwitcherRunnable implements ASTRunnable {

    private final int                     selectionOffset;
    private final int                     selectionLength;
    private ICPPASTCompositeTypeSpecifier compositeTypeSpecifier;

    public StructClassSwitcherRunnable(int selectionOffset, int selectionLength) {
        this.selectionOffset = selectionOffset;
        this.selectionLength = selectionLength;
    }

    public ICPPASTCompositeTypeSpecifier getCompositeTypeSpecifier() {
        return compositeTypeSpecifier;
    }

    @Override
    public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
        IASTNodeSelector nodeSelector = ast.getNodeSelector(null);
        IASTNode node = nodeSelector.findEnclosingNode(selectionOffset, selectionLength);
        IASTNode originNode = null;
        while (node != null) {
            if (node instanceof ICPPASTCompositeTypeSpecifier) {
                compositeTypeSpecifier = (ICPPASTCompositeTypeSpecifier) node;
                if ((compositeTypeSpecifier.getKey() == IASTCompositeTypeSpecifier.k_struct //
                     || compositeTypeSpecifier.getKey() == ICPPASTCompositeTypeSpecifier.k_class) //
                    && (isNotMemberOf(originNode, compositeTypeSpecifier) || isPrologVisibilityLabel(originNode, compositeTypeSpecifier))) {
                    return Status.OK_STATUS;
                } else {
                    return Status.CANCEL_STATUS;
                }
            }
            originNode = node;
            node = node.getParent();
        }
        return Status.CANCEL_STATUS;
    }

    private boolean isNotMemberOf(IASTNode node, ICPPASTCompositeTypeSpecifier compositeTypeSpecifier) {
        return Arrays.stream(compositeTypeSpecifier.getMembers()) //
                .allMatch(member -> member != node);
    }

    private boolean isPrologVisibilityLabel(IASTNode node, ICPPASTCompositeTypeSpecifier compositeTypeSpecifier) {
        return Arrays.stream(compositeTypeSpecifier.getMembers()) //
                .findFirst() //
                .filter(member -> member == node) //
                .filter(ICPPASTVisibilityLabel.class::isInstance) //
                .isPresent();
    }
}
