package com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;


public class ES75AvoidDoStatementsVisitor extends BaseVisitor {

    public ES75AvoidDoStatementsVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof IASTDoStatement && nodeHasNoIgnoreAttribute(this, statement)) {
            checker.reportProblem(ProblemId.P_ES75, statement);
        }
        return super.visit(statement);
    }
}
