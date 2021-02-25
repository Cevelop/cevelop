package com.cevelop.gslator.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;


public class C83_02SwapParamIsNotReferenceQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        return Rule.C83 + ": Change Parameter of swap function to a reference";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) {
            return false;
        }
        return getMarkedNode(marker) instanceof IASTFunctionDeclarator;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTDeclarator oldParamDeclarator = null;
        for (IASTNode iastnode : markedNode.getChildren()) {
            if (iastnode instanceof ICPPASTParameterDeclaration) {
                for (IASTNode subiastnode : iastnode.getChildren()) {
                    if (subiastnode instanceof ICPPASTDeclarator) {
                        oldParamDeclarator = (IASTDeclarator) subiastnode;
                    }
                }
            }
        }
        if (oldParamDeclarator != null) {
            IASTDeclarator newParamDeclarator = oldParamDeclarator.copy(CopyStyle.withLocations);
            newParamDeclarator.addPointerOperator(ASTFactory.newReferenceOperator());
            hRewrite.replace(oldParamDeclarator, newParamDeclarator, null);
        }
    }
}
