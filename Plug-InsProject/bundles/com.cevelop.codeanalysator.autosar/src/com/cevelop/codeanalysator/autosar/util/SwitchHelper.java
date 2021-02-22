package com.cevelop.codeanalysator.autosar.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSwitchStatement;


public class SwitchHelper {

    public static SwitchBody parseBody(ICPPASTSwitchStatement switchStatement) {
        List<IASTStatement> switchStatements = getStatementsOfSwitch(switchStatement);
        List<SwitchClause> clauses = new ArrayList<>();
        ListIterator<IASTStatement> statementIterator = switchStatements.listIterator();
        while (statementIterator.hasNext()) {
            SwitchClause clause = parseClause(statementIterator);
            if (!clause.isTrivial()) {
                return SwitchBody.nonTrivialSwitchBody();
            }
            clauses.add(clause);
        }
        return SwitchBody.trivialSwitchBody(clauses);
    }

    private static List<IASTStatement> getStatementsOfSwitch(ICPPASTSwitchStatement switchStatement) {
        IASTStatement switchBody = switchStatement.getBody();
        if (switchBody instanceof IASTCompoundStatement) {
            IASTCompoundStatement switchCompoundStatement = (IASTCompoundStatement) switchBody;
            return Arrays.asList(switchCompoundStatement.getStatements());
        } else {
            List<IASTStatement> switchStatements = new ArrayList<>();
            switchStatements.add(switchBody);
            return switchStatements;
        }
    }

    private static SwitchClause parseClause(ListIterator<IASTStatement> statementIterator) {
        IASTStatement statement = peek(statementIterator);
        if (statement instanceof IASTCaseStatement) {
            return parseCaseClause(statementIterator);
        } else if (statement instanceof IASTDefaultStatement) {
            return parseDefaultClause(statementIterator);
        } else {
            return SwitchClause.noneClause();
        }
    }

    private static IASTStatement peek(ListIterator<IASTStatement> statementIterator) {
        IASTStatement statement = statementIterator.next();
        statementIterator.previous();
        return statement;
    }

    private static SwitchClause parseCaseClause(ListIterator<IASTStatement> statementIterator) {
        IASTCaseStatement caseStatement = (IASTCaseStatement) statementIterator.next();
        IASTExpression caseExpression = caseStatement.getExpression();
        ClauseBody body = parseClauseBody(statementIterator);
        return SwitchClause.caseClause(caseExpression, body);
    }

    private static SwitchClause parseDefaultClause(ListIterator<IASTStatement> statementIterator) {
        statementIterator.next();
        ClauseBody body = parseClauseBody(statementIterator);
        return SwitchClause.defaultClause(body);
    }

    private static ClauseBody parseClauseBody(ListIterator<IASTStatement> statementIterator) {
        List<IASTStatement> statementsOfClause = new ArrayList<>();
        boolean isFallThrough = false;
        while (statementIterator.hasNext()) {
            IASTStatement statement = statementIterator.next();
            if (statement instanceof IASTBreakStatement) {
                break;
            } else if (isClauseStatement(statement)) {
                isFallThrough = true;
                break;
            }
            if (hasNestedBreakStatementOrClause(statement)) {
                return ClauseBody.nonTrivialBody();
            }
            statementsOfClause.add(statement);
            if (statement instanceof IASTReturnStatement) {
                break;
            }
        }
        if (isFallThrough) {
            statementIterator.previous(); // leave case or default statement for next iteration
        }
        return ClauseBody.trivialBody(statementsOfClause, isFallThrough);
    }

    private static boolean hasNestedBreakStatementOrClause(IASTStatement statement) {
        NestedBreakVisitor breakVisitor = new NestedBreakVisitor();
        statement.accept(breakVisitor);
        if (breakVisitor.hasFoundNestedBreakStatement()) {
            return true;
        }
        NestedClauseVisitor clauseVisitor = new NestedClauseVisitor();
        statement.accept(clauseVisitor);
        return clauseVisitor.hasFoundNestedClause();
    }

    private static boolean isClauseStatement(IASTStatement statement) {
        return statement instanceof IASTCaseStatement || statement instanceof IASTDefaultStatement;
    }

    public static class SwitchBody {

        private final boolean            isTrivial;
        private final List<SwitchClause> clauses;

        private SwitchBody(boolean isTrivial, List<SwitchClause> clauses) {
            this.isTrivial = isTrivial;
            this.clauses = clauses;
        }

        public static SwitchBody trivialSwitchBody(List<SwitchClause> clauses) {
            return new SwitchBody(true, clauses);
        }

        public static SwitchBody nonTrivialSwitchBody() {
            return new SwitchBody(false, null);
        }

        public boolean isTrivial() {
            return isTrivial;
        }

        public List<SwitchClause> getClauses() {
            if (!isTrivial) {
                throw new IllegalStateException("clauses are not available for non-trivial switch bodies.");
            }
            return clauses;
        }
    }

    public static class SwitchClause {

        private final ClauseKind          clauseType;
        private final IASTExpression      caseExpression;
        private final boolean             isTrivial;
        private final List<IASTStatement> statements;
        private final boolean             isFallThrough;

        private SwitchClause(ClauseKind clauseType, IASTExpression caseExpression, ClauseBody body) {
            this.clauseType = clauseType;
            this.caseExpression = caseExpression;
            if (body != null) {
                isTrivial = body.isTrivial();
                if (isTrivial) {
                    statements = body.getStatements();
                    isFallThrough = body.isFallThrough();
                } else {
                    statements = null;
                    isFallThrough = false;
                }
            } else {
                isTrivial = false;
                statements = null;
                isFallThrough = false;
            }
        }

        public static SwitchClause caseClause(IASTExpression caseExpression, ClauseBody body) {
            return new SwitchClause(ClauseKind.CASE, caseExpression, body);
        }

        public static SwitchClause defaultClause(ClauseBody body) {
            return new SwitchClause(ClauseKind.DEFAULT, null, body);
        }

        public static SwitchClause noneClause() {
            return new SwitchClause(ClauseKind.NONE, null, null);
        }

        public ClauseKind getClauseKind() {
            return clauseType;
        }

        public IASTExpression getCaseExpression() {
            return caseExpression;
        }

        public boolean isTrivial() {
            return isTrivial;
        }

        public List<IASTStatement> getStatements() {
            if (!isTrivial) {
                throw new IllegalStateException("statements are not available for non-trivial clauses.");
            }
            return statements;
        }

        public boolean isFallThrough() {
            if (!isTrivial) {
                throw new IllegalStateException("whether the clause is falling through is not available for non-trivial clauses.");
            }
            return isFallThrough;
        }
    }

    public static enum ClauseKind {
        NONE, CASE, DEFAULT
    }

    private static class ClauseBody {

        private final boolean             isTrivial;
        private final List<IASTStatement> statements;
        private final boolean             isFallThrough;

        private ClauseBody(boolean isTrivial, List<IASTStatement> statements, boolean isFallThrough) {
            this.isTrivial = isTrivial;
            this.statements = statements;
            this.isFallThrough = isFallThrough;
        }

        public static ClauseBody trivialBody(List<IASTStatement> statements, boolean isFallThrough) {
            return new ClauseBody(true, statements, isFallThrough);
        }

        public static ClauseBody nonTrivialBody() {
            return new ClauseBody(false, null, false);
        }

        public boolean isTrivial() {
            return isTrivial;
        }

        public List<IASTStatement> getStatements() {
            if (!isTrivial) {
                throw new IllegalStateException("statements are not available for non-trivial clause bodies.");
            }
            return statements;
        }

        public boolean isFallThrough() {
            if (!isTrivial) {
                throw new IllegalStateException("whether the clause is falling through is not available for non-trivial clause bodies.");
            }
            return isFallThrough;
        }
    }

    private static class NestedBreakVisitor extends ASTVisitor {

        private boolean foundNestedBreakStatement = false;

        public NestedBreakVisitor() {
            shouldVisitStatements = true;
        }

        public boolean hasFoundNestedBreakStatement() {
            return foundNestedBreakStatement;
        }

        @Override
        public int visit(IASTStatement statement) {
            if (isBreakOwningStatement(statement)) {
                return PROCESS_SKIP;
            } else if (statement instanceof IASTBreakStatement) {
                foundNestedBreakStatement = true;
                return PROCESS_ABORT;
            } else {
                return PROCESS_CONTINUE;
            }
        }

        private boolean isBreakOwningStatement(IASTStatement statement) {
            return statement instanceof IASTWhileStatement || statement instanceof IASTForStatement || statement instanceof IASTDoStatement ||
                   statement instanceof ICPPASTRangeBasedForStatement || statement instanceof IASTSwitchStatement;
        }
    }

    private static class NestedClauseVisitor extends ASTVisitor {

        private boolean foundNestedClause = false;

        public NestedClauseVisitor() {
            shouldVisitStatements = true;
        }

        public boolean hasFoundNestedClause() {
            return foundNestedClause;
        }

        @Override
        public int visit(IASTStatement statement) {
            if (isClauseOwningStatement(statement)) {
                return PROCESS_SKIP;
            } else if (isClauseStatement(statement)) {
                foundNestedClause = true;
                return PROCESS_ABORT;
            } else {
                return PROCESS_CONTINUE;
            }
        }

        private boolean isClauseOwningStatement(IASTStatement statement) {
            return statement instanceof IASTSwitchStatement;
        }
    }
}
