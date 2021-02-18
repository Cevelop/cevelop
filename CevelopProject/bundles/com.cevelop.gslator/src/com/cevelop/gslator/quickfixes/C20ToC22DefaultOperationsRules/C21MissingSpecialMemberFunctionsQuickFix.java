package com.cevelop.gslator.quickfixes.C20ToC22DefaultOperationsRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class C21MissingSpecialMemberFunctionsQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C21.getId())) return Rule.C21.toString() + ": Define missing special member functions";
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        final ICPPASTCompositeTypeSpecifier struct = ASTHelper.getCompositeTypeSpecifier(markedNode);

        final List<IASTNode> missingNodes = createMissingNodeList(struct);

        insertNodesUnderVisibilityLabel(hRewrite, struct, missingNodes, ICPPASTVisibilityLabel.v_public);
    }

    private List<IASTNode> createMissingNodeList(final ICPPASTCompositeTypeSpecifier struct) {
        final List<IASTNode> missingNodes = new ArrayList<>();

        if (hasNo(struct, ASTHelper.SpecialFunction.DefaultConstructor)) {
            missingNodes.add(ASTFactory.newDefaultConstructor(struct.getName()));
        }
        if (hasNo(struct, ASTHelper.SpecialFunction.DefaultCopyAssignment)) {
            missingNodes.add(ASTFactory.newDefaultCopyAssignment(struct.getName()));
        }
        if (hasNo(struct, ASTHelper.SpecialFunction.DefaultCopyConstructor)) {
            missingNodes.add(ASTFactory.newDefaultCopyConstructor(struct.getName()));
        }
        if (hasNo(struct, ASTHelper.SpecialFunction.MoveAssignment)) {
            missingNodes.add(ASTFactory.newDefaultMoveAssignment(struct.getName()));
        }
        if (hasNo(struct, ASTHelper.SpecialFunction.MoveConstructor)) {
            missingNodes.add(ASTFactory.newDefaultMoveConstructor(struct.getName()));
        }
        if (hasNo(struct, ASTHelper.SpecialFunction.DefaultDestructor)) {
            missingNodes.add(ASTFactory.newDefaultNoexceptDestructor(struct.getName()));
        }
        return missingNodes;
    }

    private boolean hasNo(final ICPPASTCompositeTypeSpecifier struct, final ASTHelper.SpecialFunction type) {
        return ASTHelper.getFirstSpecialMemberFunction(struct, type) == null;
    }

}
