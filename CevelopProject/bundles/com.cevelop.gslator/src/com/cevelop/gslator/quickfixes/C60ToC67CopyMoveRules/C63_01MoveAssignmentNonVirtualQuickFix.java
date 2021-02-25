package com.cevelop.gslator.quickfixes.C60ToC67CopyMoveRules;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


public class C63_01MoveAssignmentNonVirtualQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C63_01.getId())) {
            return Rule.C63 + ": Set to non-virtual";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTDeclSpecifier declSpec = (ICPPASTDeclSpecifier) markedNode.copy(CopyStyle.withLocations);
        declSpec.setVirtual(false);
        hRewrite.replace(markedNode, declSpec, null);
    }

}
