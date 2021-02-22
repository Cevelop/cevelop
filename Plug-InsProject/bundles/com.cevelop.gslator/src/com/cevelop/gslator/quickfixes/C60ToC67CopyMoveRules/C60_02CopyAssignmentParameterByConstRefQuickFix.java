package com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


public class C60_02CopyAssignmentParameterByConstRefQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C60_02.getId())) {
            return Rule.C60 + ": Set parameter to const";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTParameterDeclaration replacement = (ICPPASTParameterDeclaration) markedNode.copy(CopyStyle.withLocations);
        replacement.getDeclSpecifier().setConst(true);
        hRewrite.replace(markedNode, replacement, null);
    }

}
