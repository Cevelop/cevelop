package com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.ES70toES86StatementRules.ES76AvoidGotoChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils.ES76GotoUsagePattern;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;


public class ES76AvoidGotoVisitor extends BaseVisitor {

    public ES76AvoidGotoVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof IASTGotoStatement && nodeHasNoIgnoreAttribute(this, statement)) {
            IASTGotoStatement gotostmt = (IASTGotoStatement) statement;
            switch (ES76GotoUsagePattern.getPattern(gotostmt)) {
            case IF:
                checker.reportProblem(ProblemId.P_ES76, gotostmt, new GslatorInfo(" (normal if should work here)"));
                break;
            case LOOP:
                checker.reportProblem(ProblemId.P_ES76, gotostmt, new GslatorInfo(" (while loop should work here)"));
                break;
            case BREAK:
                checker.reportProblem(ProblemId.P_ES76, gotostmt, new GslatorInfo(" (simple break should work here)"));
                break;
            case LAMBDA:
                if (((ES76AvoidGotoChecker) checker).isMultibreakMarkerEnabled(statement.getTranslationUnit().getOriginatingTranslationUnit()
                        .getResource())) checker.reportProblem(ProblemId.P_ES76, gotostmt, new GslatorInfo(
                                " (lambda and return might be more elegant)"));
                break;
            default:
                checker.reportProblem(ProblemId.P_ES76, statement, new GslatorInfo(""));
                break;
            }
        }
        return super.visit(statement);
    }
}
