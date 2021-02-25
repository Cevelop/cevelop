package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


public class C37C44C66C84ShouldBeNoExceptQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C37.getId())) {
            return Rule.C37 + ": Set to noexcept";
        } else if (problemId.contentEquals(ProblemId.P_C44.getId())) {
            return Rule.C44 + ": Set to noexcept";
        } else if (problemId.contentEquals(ProblemId.P_C66.getId())) {
            return Rule.C66 + ": Set to noexcept";
        } else if (problemId.contentEquals(ProblemId.P_C84.getId())) {
            return Rule.C84 + ": Set to noexcept";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTFunctionDeclarator decl = (ICPPASTFunctionDeclarator) markedNode.copy(CopyStyle.withLocations);
        decl.setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);
        hRewrite.replace(markedNode, decl, null);

        if (markedNode.getParent() instanceof IASTSimpleDeclaration) {
            replaceImplementation(markedNode);
        }
    }

    private void replaceImplementation(final IASTNode markedNode) {
        final ICPPASTFunctionDefinition impl = getImplFromDeclaration((IASTSimpleDeclaration) markedNode.getParent());

        if (impl != null) {
            final IASTFunctionDeclarator oldDecl = impl.getDeclarator();
            final ICPPASTFunctionDeclarator newDecl = (ICPPASTFunctionDeclarator) oldDecl.copy(CopyStyle.withLocations);

            newDecl.setNoexceptExpression(ICPPASTFunctionDeclarator.NOEXCEPT_DEFAULT);

            astRewriteStore.getASTRewrite(impl).replace(oldDecl, newDecl, null);
        }
    }

}
