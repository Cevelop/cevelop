package com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


public class C63_02MoveAssignmentReturnByNonConstRefQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C63_02.getId())) {
            return Rule.C63 + ": Set return type to non-const reference";
        }
        return BaseQuickFix.FAIL;
    }

    @SuppressWarnings("restriction")
    @Override
    protected void handleMarkedNode(IASTNode markedNode, final ASTRewrite hRewrite) {
        markedNode = getSimpleDeclarationOrFunctionDefinition(markedNode);
        final IASTDeclaration replacement = (IASTDeclaration) markedNode.copy(CopyStyle.withLocations);

        final ICPPASTNamedTypeSpecifier declSpec = (ICPPASTNamedTypeSpecifier) ASTHelper.getDeclSpecifierFromDeclaration(replacement);
        final ICPPASTFunctionDeclarator declarator = ASTHelper.getFunctionDeclaratorFromDeclarationOrDefinition(replacement);

        declSpec.setConst(false);

        // TODO ReferenceOperator missing?
        if (!(declarator.getPointerOperators().length == 1 && declarator.getPointerOperators()[0] instanceof ICPPASTReferenceOperator)) {
            declarator.addPointerOperator(factory.newReferenceOperator());
        }

        hRewrite.replace(markedNode, replacement, null);
    }

}
