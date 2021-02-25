package com.cevelop.codeanalysator.core.visitor.shared;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.helper.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.util.ASTHelper;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DeclareLoopVariableInTheInitializerVisitor extends CodeAnalysatorVisitor {

    public DeclareLoopVariableInTheInitializerVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(final IASTStatement statement) {
        if (!isHighestPriorityRuleForNode(statement)) {
            return PROCESS_CONTINUE;
        }
        if (statement instanceof IASTForStatement) {
            final IASTForStatement forStatement = (IASTForStatement) statement;

            if (isRuleSuppressedForNode(forStatement)) {
                return PROCESS_CONTINUE;
            }
            if (forStatement.getInitializerStatement() instanceof IASTDeclarationStatement) {
                return PROCESS_CONTINUE;
            }
            if (forStatement.getIterationExpression() instanceof IASTExpressionList) {
                return PROCESS_CONTINUE;
            }

            IASTName loopVariable = ASTHelper.getLoopVariable(forStatement);
            if (loopVariable == null) {
                return PROCESS_CONTINUE;
            }

            String contextFlagsString = createContextFlagsString(forStatement, loopVariable);
            reportRuleForNode(forStatement, contextFlagsString);
        }
        return PROCESS_CONTINUE;
    }

    private String createContextFlagsString(IASTForStatement forStatement, IASTName loopVariable) {
        IASTTranslationUnit translatioUnit = forStatement.getTranslationUnit();

        IBinding loopBinding = loopVariable.resolveBinding();
        IASTName[] variableReferences = translatioUnit.getReferences(loopBinding);

        return isValidReferences(variableReferences) ? ContextFlagsHelper.DeclareLoopVariableInTheInitializerContextFlagValidReferences : "";
    }

    private boolean isValidReferences(final IASTName[] references) {
        for (IASTName name : references) {
            if (!(ASTHelper.isForLoopStatement(name)) && !(isLeftSideReference(name))) {
                return false;
            }
        }
        return true;
    }

    private boolean isLeftSideReference(IASTName name) {
        IASTIdExpression idExpression = (IASTIdExpression) name.getParent();
        IASTNode parent = idExpression.getParent();
        if (parent instanceof IASTBinaryExpression) {
            IASTBinaryExpression binaryExpression = (IASTBinaryExpression) name.getParent().getParent();
            if (binaryExpression.getOperand1().equals(idExpression)) {
                return true;
            }
        }
        return false;
    }
}
