package com.cevelop.codeanalysator.autosar.quickfix;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSwitchStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.autosar.util.ScopeHelper;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper.ClauseKind;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper.SwitchBody;
import com.cevelop.codeanalysator.autosar.util.SwitchHelper.SwitchClause;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class SwitchMustHaveAtLeastTwoCasesQuickFix extends BaseQuickFix {

    public SwitchMustHaveAtLeastTwoCasesQuickFix(String label) {
        super(label);
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) {
            return false;
        }

        String contextFlagsString = getProblemArgument(marker, ContextFlagsHelper.SwitchMustHaveAtLeastTwoCasesContextFlagsStringIndex);
        return contextFlagsString.contains(ContextFlagsHelper.SwitchMustHaveAtLeastTwoCasesContextFlagTrivial);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTSwitchStatement)) {
            return;
        }
        ICPPASTSwitchStatement switchStatement = (ICPPASTSwitchStatement) markedNode;
        SwitchWithLessThanTwoCasesReplacer replacer = new SwitchWithLessThanTwoCasesReplacer(factory, switchStatement);
        replacer.replace(hRewrite);
    }

    private static class SwitchWithLessThanTwoCasesReplacer {

        private final IBetterFactory         factory;
        private final ICPPASTSwitchStatement switchStatement;
        private final List<IASTStatement>    replacementStatements = new ArrayList<>();

        public SwitchWithLessThanTwoCasesReplacer(IBetterFactory factory, ICPPASTSwitchStatement switchStatement) {
            this.factory = factory;
            this.switchStatement = switchStatement;
        }

        public void replace(ASTRewrite hRewrite) {
            replaceInitStatement();
            replaceControllerDeclaration();
            replaceBodyAndEvaluateControllerExpression();
            surroundWithCompoundStatementIfIdentifiersWouldCollide();
            rewriteWithReplacementStatements(hRewrite);
        }

        private void replaceInitStatement() {
            IASTStatement initStatement = switchStatement.getInitializerStatement();
            if (initStatement != null) {
                IASTStatement newInitStatement = initStatement.copy(CopyStyle.withoutLocations);
                replacementStatements.add(newInitStatement);
            }
        }

        private void replaceControllerDeclaration() {
            IASTDeclaration controllerDeclaration = switchStatement.getControllerDeclaration();
            if (controllerDeclaration != null) {
                IASTDeclaration newControllerDeclaration = controllerDeclaration.copy(CopyStyle.withoutLocations);
                IASTStatement controllerDeclarationStatement = factory.newDeclarationStatement(newControllerDeclaration);
                replacementStatements.add(controllerDeclarationStatement);
            }
        }

        private void replaceBodyAndEvaluateControllerExpression() {
            IASTExpression controllerExpression = createReplacementControllerExpression();
            SwitchBody body = SwitchHelper.parseBody(switchStatement);
            List<SwitchClause> clauses = body.getClauses();
            if (clauses.size() == 0) {
                replaceEmptySwitchBody(controllerExpression);
            } else if (clauses.size() == 1) {
                SwitchClause onlyClause = clauses.get(0);
                if (onlyClause.getClauseKind() == ClauseKind.CASE) {
                    IASTExpression caseExpression = onlyClause.getCaseExpression().copy();
                    replaceCaseOnlySwitchBody(controllerExpression, caseExpression, onlyClause);
                } else {
                    replaceDefaultOnlySwitchBody(controllerExpression, onlyClause);
                }
            } else if (clauses.size() == 2) {
                SwitchClause firstClause = clauses.get(0);
                SwitchClause secondClause = clauses.get(1);
                if (firstClause.getClauseKind() == ClauseKind.CASE) {
                    IASTExpression caseExpression = firstClause.getCaseExpression().copy();
                    if (firstClause.isFallThrough()) {
                        replaceCaseToDefaultFallThroughSwitchBody(controllerExpression, caseExpression, firstClause, secondClause);
                    } else {
                        replaceCaseDefaultSwitchBody(controllerExpression, caseExpression, firstClause, secondClause);
                    }
                } else {
                    IASTExpression caseExpression = secondClause.getCaseExpression().copy();
                    if (firstClause.isFallThrough()) {
                        replaceDefaultToCaseFallThroughSwitchBody(controllerExpression, caseExpression, secondClause, firstClause);
                    } else {
                        replaceCaseDefaultSwitchBody(controllerExpression, caseExpression, secondClause, firstClause);
                    }
                }
            }
        }

        private IASTExpression createReplacementControllerExpression() {
            IASTExpression controllerExpression = switchStatement.getControllerExpression();
            if (controllerExpression != null) {
                return controllerExpression.copy(CopyStyle.withoutLocations);
            } else {
                IASTSimpleDeclaration controllerDeclaration = (IASTSimpleDeclaration) switchStatement.getControllerDeclaration();
                IASTDeclarator controllerDeclarator = controllerDeclaration.getDeclarators()[0];
                IASTName controllerVariableIdentifier = controllerDeclarator.getName();
                return factory.newIdExpression(controllerVariableIdentifier.copy(CopyStyle.withoutLocations));
            }
        }

        private void replaceEmptySwitchBody(IASTExpression controllerExpresion) {
            evaluateControllerExpressionIfNeeded(controllerExpresion);
        }

        private void replaceCaseOnlySwitchBody(IASTExpression controllerExpression, IASTExpression caseExpression, SwitchClause caseClause) {
            addIfElseClause(controllerExpression, IASTBinaryExpression.op_equals, caseExpression, caseClause, null);
        }

        private void replaceDefaultOnlySwitchBody(IASTExpression replacementControllerExpression, SwitchClause defaultClause) {
            evaluateControllerExpressionIfNeeded(replacementControllerExpression);
            addFollowingClause(defaultClause);
        }

        private void replaceDefaultToCaseFallThroughSwitchBody(IASTExpression controllerExpression, IASTExpression caseExpression,
                SwitchClause caseClause, SwitchClause defaultClause) {
            addIfElseClause(controllerExpression, IASTBinaryExpression.op_notequals, caseExpression, defaultClause, null);
            addFollowingClause(caseClause);
        }

        private void replaceCaseToDefaultFallThroughSwitchBody(IASTExpression controllerExpression, IASTExpression caseExpression,
                SwitchClause caseClause, SwitchClause defaultClause) {
            addIfElseClause(controllerExpression, IASTBinaryExpression.op_equals, caseExpression, caseClause, null);
            addFollowingClause(defaultClause);
        }

        private void replaceCaseDefaultSwitchBody(IASTExpression controllerExpression, IASTExpression caseExpression, SwitchClause caseClause,
                SwitchClause defaultClause) {
            addIfElseClause(controllerExpression, IASTBinaryExpression.op_equals, caseExpression, caseClause, defaultClause);
        }

        private void addIfElseClause(IASTExpression controllerExpression, int comparisonOperator, IASTExpression caseExpression,
                SwitchClause ifClause, SwitchClause elseClause) {
            IASTBinaryExpression condition = factory.newBinaryExpression(comparisonOperator, controllerExpression, caseExpression);
            IASTCompoundStatement ifCompoundStatement = createCompoundStatementFromClause(ifClause);
            IASTCompoundStatement elseCompoundStatement = elseClause != null ? createCompoundStatementFromClause(elseClause) : null;
            IASTIfStatement ifStatement = factory.newIfStatement(condition, ifCompoundStatement, elseCompoundStatement);
            replacementStatements.add(ifStatement);
        }

        private void addFollowingClause(SwitchClause followingClause) {
            followingClause.getStatements().stream() //
                    .map(IASTStatement::copy) //
                    .forEachOrdered(replacementStatements::add);
        }

        private void evaluateControllerExpressionIfNeeded(IASTExpression controllerExpression) {
            if (needToEvaluateControllerExpression(controllerExpression)) {
                evaluateControllerExpression(controllerExpression);
            }
        }

        private boolean needToEvaluateControllerExpression(IASTExpression controllerExpression) {
            return !(controllerExpression instanceof IASTIdExpression);
        }

        private void evaluateControllerExpression(IASTExpression controllerExpression) {
            IASTStatement controllerExpressionStatement = factory.newExpressionStatement(controllerExpression);
            replacementStatements.add(controllerExpressionStatement);
        }

        private IASTCompoundStatement createCompoundStatementFromClause(SwitchClause clause) {
            IASTCompoundStatement compoundStatement = factory.newCompoundStatement();
            clause.getStatements().stream() //
                    .map(IASTStatement::copy) //
                    .forEachOrdered(compoundStatement::addStatement);
            return compoundStatement;
        }

        private void surroundWithCompoundStatementIfIdentifiersWouldCollide() {
            if (wouldIdentifierCollideWithSurroundingScope()) {
                surroundWithCompoundStatement();
            }
        }

        private boolean wouldIdentifierCollideWithSurroundingScope() {
            IScope surroundingScope = ScopeHelper.getParentScope(switchStatement.getScope());
            if (surroundingScope == null) {
                return true; // cannot check without scope, assume an identifier would collide
            }
            BlockScopeIdentifierCollectorVisitor identifierCollectorVisitor = new BlockScopeIdentifierCollectorVisitor();
            replacementStatements.stream().forEach(statement -> statement.accept(identifierCollectorVisitor));
            return identifierCollectorVisitor.getCollectedIdentifiers().stream() //
                    .anyMatch(identifier -> doesExistInScope(identifier, surroundingScope));
        }

        private boolean doesExistInScope(IASTName identifier, IScope surroundingScope) {
            return surroundingScope.getBinding(identifier, true) != null;
        }

        private void surroundWithCompoundStatement() {
            IASTCompoundStatement surroundingCompoundStatement = factory.newCompoundStatement();
            replacementStatements.stream().forEachOrdered(surroundingCompoundStatement::addStatement);
            replacementStatements.clear();
            replacementStatements.add(surroundingCompoundStatement);
        }

        private void rewriteWithReplacementStatements(ASTRewrite hRewrite) {
            if (replacementStatements.size() == 1) {
                hRewrite.replace(switchStatement, replacementStatements.get(0), null);
            } else {
                replacementStatements.stream() //
                        .forEachOrdered(statement -> hRewrite.insertBefore(switchStatement.getParent(), switchStatement, statement, null));
                hRewrite.remove(switchStatement, null);
            }
        }
    }

    private static class BlockScopeIdentifierCollectorVisitor extends ASTVisitor {

        private final Set<IASTName> collectedIdentifiers = new LinkedHashSet<>();

        public BlockScopeIdentifierCollectorVisitor() {
            shouldVisitStatements = true;
            shouldVisitDeclarators = true;
        }

        public Set<IASTName> getCollectedIdentifiers() {
            return collectedIdentifiers;
        }

        @Override
        public int visit(IASTStatement statement) {
            if (statement instanceof IASTCompoundStatement) {
                return PROCESS_SKIP; // skip identifiers in child block scopes
            }
            return PROCESS_CONTINUE;
        }

        @Override
        public int visit(IASTDeclarator declarator) {
            collectedIdentifiers.add(declarator.getName());
            return PROCESS_CONTINUE;
        }
    }
}
