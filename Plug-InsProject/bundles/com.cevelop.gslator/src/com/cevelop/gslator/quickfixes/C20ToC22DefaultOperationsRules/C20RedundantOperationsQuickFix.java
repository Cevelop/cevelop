package com.cevelop.gslator.quickfixes.C20ToC22DefaultOperationsRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


@SuppressWarnings("restriction")
public class C20RedundantOperationsQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C20.getId())) {
            return Rule.C20.toString() + ": Set to = default";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, ASTRewrite hRewrite) {
        IASTDeclaration decl = getSimpleDeclarationOrFunctionDefinition(markedNode);

        if (decl instanceof IASTSimpleDeclaration) {
            decl = getImplFromDeclaration((IASTSimpleDeclaration) decl);
            hRewrite = astRewriteStore.getASTRewrite(decl);
        }

        if (decl instanceof ICPPASTFunctionDefinition) {
            final ICPPASTFunctionDefinition function = (ICPPASTFunctionDefinition) decl;
            final ICPPASTFunctionDefinition newNode = factory.newFunctionDefinition(function.getDeclSpecifier().copy(CopyStyle.withLocations),
                    function.getDeclarator().copy(CopyStyle.withLocations), null);
            newNode.setIsDefaulted(true);

            hRewrite.replace(function, newNode, null);
        }
    }
}
