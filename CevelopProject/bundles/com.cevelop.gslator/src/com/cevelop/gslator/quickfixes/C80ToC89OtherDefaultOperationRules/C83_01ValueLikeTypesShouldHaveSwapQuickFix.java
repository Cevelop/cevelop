package com.cevelop.gslator.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.nodes.ASTComment;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class C83_01ValueLikeTypesShouldHaveSwapQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        return Rule.C83 + ": Add Swap Member Function";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        return getMarkedNode(marker) instanceof IASTName;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        ICPPASTCompositeTypeSpecifier oldnode = ASTHelper.getCompositeTypeSpecifier(markedNode);
        ICPPASTFunctionDefinition swap = ASTFactory.newSwapFunction(oldnode.getName());

        IASTNode commentnode = null;
        for (IASTNode swapchild : swap.getChildren()) {
            if (swapchild instanceof ICPPASTCompoundStatement) {
                for (IASTNode compchild : swapchild.getChildren()) {
                    if (compchild instanceof IASTDeclarationStatement) {
                        commentnode = compchild;
                    }
                }
            }
        }

        if (commentnode != null) {
            ASTComment comment = new ASTComment();
            comment.setComment("TODO Auto-generated method stub\n".toCharArray());
            hRewrite.addComment(commentnode, comment, ASTRewrite.CommentPosition.trailing);
        }

        if (!hasInclude("utility", oldnode.getTranslationUnit())) {
            newHeaders.add("utility");
        }
        hRewrite.insertBefore(oldnode, null, swap, null);
    }
}
