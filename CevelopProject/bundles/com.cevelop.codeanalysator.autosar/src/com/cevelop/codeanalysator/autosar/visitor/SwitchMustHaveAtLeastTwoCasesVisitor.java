package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSwitchStatement;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper.SwitchBody;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class SwitchMustHaveAtLeastTwoCasesVisitor extends CodeAnalysatorVisitor {

    public SwitchMustHaveAtLeastTwoCasesVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitStatements = true;
    }

    @Override
    public int visit(IASTStatement statement) {
        if (statement instanceof ICPPASTSwitchStatement) {
            ICPPASTSwitchStatement switchStatement = (ICPPASTSwitchStatement) statement;
            IASTNode body = switchStatement.getBody();
            CountSwitchCasesVisitor countCasesVisitor = new CountSwitchCasesVisitor();
            body.accept(countCasesVisitor);
            if (countCasesVisitor.getNumberOfCases() < 2) {
                String contextFlagsString = createContextFlagsString(switchStatement);
                reportRuleForNode(statement, contextFlagsString);
            }
        }
        return super.visit(statement);
    }

    private String createContextFlagsString(ICPPASTSwitchStatement switchStatement) {
        SwitchBody body = SwitchHelper.parseBody(switchStatement);
        return body.isTrivial() ? ContextFlagsHelper.SwitchMustHaveAtLeastTwoCasesContextFlagTrivial : "";
    }

    private static class CountSwitchCasesVisitor extends ASTVisitor {

        private long numberOfCases = 0;

        public CountSwitchCasesVisitor() {
            shouldVisitStatements = true;
        }

        public long getNumberOfCases() {
            return numberOfCases;
        }

        @Override
        public int visit(IASTStatement statement) {
            if (statement instanceof IASTSwitchStatement) {
                return PROCESS_SKIP;
            } else if (statement instanceof IASTCaseStatement) {
                numberOfCases++;
            }
            return PROCESS_CONTINUE;
        }
    }
}
