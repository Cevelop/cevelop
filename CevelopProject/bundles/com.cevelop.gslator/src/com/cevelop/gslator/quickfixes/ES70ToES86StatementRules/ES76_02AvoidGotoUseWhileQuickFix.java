package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils.ES76GotoUsagePattern;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class ES76_02AvoidGotoUseWhileQuickFix extends ES76_00AvoidGotoQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES76 + ": change to normal while loop";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        final IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) return false;
        return ES76GotoUsagePattern.getPattern((IASTGotoStatement) markedNode) == ES76GotoUsagePattern.LOOP;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTLabelStatement label = getLabelStatement((IASTGotoStatement) markedNode);
        if (label != null) {
            // Collect needed Nodes
            IASTIfStatement parentIf = getIfStatement(markedNode);
            List<IASTNode> optionalNodes = collectNodesBetween(label, parentIf);

            List<IASTStatement> loopStatements = nodeListToCopiedStatements(optionalNodes);

            // Prepare new Nodes
            IASTExpression newCond = parentIf.getConditionExpression().copy(CopyStyle.withLocations);
            IASTDoStatement doStmt = ASTFactory.newDoStatement(ASTFactory.newCompoundStatement(loopStatements), newCond);

            IASTNode[] doRewriteData = ES75AvoidDoStatementsQuickFix.getDeclarationAndWhileStatementToReplaceDoStatement(doStmt, label);
            IASTDeclarationStatement firstRun = (IASTDeclarationStatement) doRewriteData[0];
            IASTWhileStatement whileStmt = (IASTWhileStatement) doRewriteData[1];

            // Replace old Nodes
            if (canRemoveLabel(label)) {
                hRewrite.replace(label, firstRun, null);
            } else {
                hRewrite.replace(label.getNestedStatement(), firstRun, null);
            }
            optionalNodes.remove(0); // already (in)direclty replaced
            for (IASTNode iastNode : optionalNodes) {
                hRewrite.remove(iastNode, null);
            }

            List<IASTStatement> elseStatements = nodeListToCopiedStatements(getClauseStatementList(parentIf.getElseClause()));
            IASTNode insertpoint = ASTHelper.getNextNode(parentIf);
            for (IASTStatement iastStatement : elseStatements) {
                hRewrite.insertBefore(parentIf.getParent(), insertpoint, iastStatement, null);
            }

            hRewrite.replace(parentIf, whileStmt, null);
        }
    }
}
