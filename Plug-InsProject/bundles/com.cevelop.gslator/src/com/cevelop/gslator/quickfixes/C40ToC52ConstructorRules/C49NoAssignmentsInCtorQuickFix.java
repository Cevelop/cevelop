package com.cevelop.gslator.quickfixes.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.RuleQuickFix;


@SuppressWarnings("restriction")
public class C49NoAssignmentsInCtorQuickFix extends RuleQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C49.getId())) {
            return Rule.C49 + ": Initialize member variables in constructor chain initializers";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final IASTNode markedNode, final ASTRewrite hRewrite) {
        // TODO: Either extend this functionality to generate correct code even if local params are used
        // or disable this quick fix in these situations i.e. override public boolean isApplicable(final IMarker marker)

        final ICPPASTBinaryExpression binaryExpression = (ICPPASTBinaryExpression) markedNode;

        final ICPPASTInitializerList newInitList = factory.newInitializerList();
        newInitList.addClause(findRightmostExpression(binaryExpression).copy(CopyStyle.withLocations));

        ICPPASTConstructorChainInitializer initializer = factory.newConstructorChainInitializer(fieldName(binaryExpression.getOperand1()).copy(
                CopyStyle.withLocations), newInitList);

        final ICPPASTFunctionDefinition ctorNode = findCtorDefinition(markedNode);
        final ICPPASTFunctionDefinition ctorCopy = ctorNode.copy(CopyStyle.withLocations);

        ctorCopy.addMemberInitializer(initializer);
        final IASTCompoundStatement newCompoundStatement = factory.newCompoundStatement();
        ctorCopy.setBody(newCompoundStatement);

        ASTRewrite astCtorRewrite = astRewriteStore.getASTRewrite(ctorNode);
        astCtorRewrite = astCtorRewrite.replace(ctorNode, ctorCopy, null);
        astCtorRewrite = astCtorRewrite.replace(newCompoundStatement, ctorNode.getBody(), null);

        IASTNode parent = binaryExpression.getParent();

        if (parent instanceof IASTExpressionStatement) {
            astCtorRewrite.remove(parent.getParent() instanceof IASTForStatement ? binaryExpression : parent, null);
        } else {
            astCtorRewrite.replace(binaryExpression, binaryExpression.getOperand2(), null);
        }
    }

    private static IASTName fieldName(IASTExpression field) {
        if (field instanceof ICPPASTFieldReference) {
            return ((ICPPASTFieldReference) field).getFieldName();
        }

        return ((IASTIdExpression) field).getName();
    }

    private static ICPPASTFunctionDefinition findCtorDefinition(final IASTNode markedNode) {
        IASTNode ctorNode = markedNode.getParent();

        while (!(ctorNode instanceof ICPPASTFunctionDefinition)) {
            ctorNode = ctorNode.getParent();
        }

        return (ICPPASTFunctionDefinition) ctorNode;
    }

    private static IASTExpression findRightmostExpression(ICPPASTBinaryExpression binaryExpression) {

        while (binaryExpression.getOperand2() instanceof ICPPASTBinaryExpression) {
            ICPPASTBinaryExpression rightBinaryExpression = (ICPPASTBinaryExpression) binaryExpression.getOperand2();

            if (rightBinaryExpression.getOperator() != IASTBinaryExpression.op_assign) {
                return rightBinaryExpression;
            }

            binaryExpression = rightBinaryExpression;
        }

        return binaryExpression.getOperand2();
    }
}
