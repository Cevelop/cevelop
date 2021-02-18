package com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.utils.ASTHelper;


public class ES74DeclareLoopVariableInTheIntializerVisitor extends BaseVisitor {

    public ES74DeclareLoopVariableInTheIntializerVisitor(final BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(final IASTStatement statement) {

        if (statement instanceof IASTForStatement) {
            final IASTForStatement forStatement = (IASTForStatement) statement;

            if (!nodeHasNoIgnoreAttribute(this, forStatement)) return super.visit(statement);
            if (forStatement.getInitializerStatement() instanceof IASTDeclarationStatement) return super.visit(statement);
            if (forStatement.getIterationExpression() instanceof IASTExpressionList) return super.visit(statement);

            IASTName loopVariable = ASTHelper.getLoopVariable(forStatement);
            if (loopVariable == null) return super.visit(statement);

            checker.reportProblem(ProblemId.P_ES74, forStatement.getInitializerStatement());
        }
        return super.visit(statement);
    }
}
