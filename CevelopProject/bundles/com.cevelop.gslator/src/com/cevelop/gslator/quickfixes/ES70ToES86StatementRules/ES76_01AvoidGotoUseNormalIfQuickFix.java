package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils.ES76GotoUsagePattern;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;


public class ES76_01AvoidGotoUseNormalIfQuickFix extends ES76_00AvoidGotoQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES76 + ": change to normal if clause";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        final IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) return false;
        return ES76GotoUsagePattern.getPattern((IASTGotoStatement) markedNode) == ES76GotoUsagePattern.IF;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTLabelStatement label = getLabelStatement((IASTGotoStatement) markedNode);
        if (label != null) {
            IASTIfStatement parentIf = getIfStatement(markedNode);
            List<IASTNode> optionalNodes = collectNodesBetween(label, parentIf);

            IASTIfStatement newif = prepareNewIfStatement(markedNode, parentIf, optionalNodes);

            hRewrite.replace(parentIf, newif, null);
            for (IASTNode iastNode : optionalNodes) {
                hRewrite.remove(iastNode, null);
            }

            if (canRemoveLabel(label)) {
                hRewrite.replace(label, label.getNestedStatement().copy(CopyStyle.withLocations), null);
            }
        }
    }

    private IASTIfStatement prepareNewIfStatement(IASTNode markedNode, IASTIfStatement parentIf, List<IASTNode> optionalNodes) {
        List<IASTStatement> thenStatements = prepareStatements(markedNode, optionalNodes, parentIf.getThenClause());
        IASTCompoundStatement newThenClause = ASTFactory.newCompoundStatement(thenStatements);

        List<IASTStatement> elseStatements = prepareStatements(markedNode, optionalNodes, parentIf.getElseClause());
        IASTCompoundStatement newElseClause = null;
        if (elseStatements.size() > 0) newElseClause = ASTFactory.newCompoundStatement(elseStatements);

        IASTExpression newCond = parentIf.getConditionExpression().copy(CopyStyle.withLocations);

        // swap "then" and "else" if "then" empty
        if (thenStatements.size() == 0) {
            if (!(newCond instanceof IASTIdExpression)) newCond = ASTFactory.newUnaryExpression(IASTUnaryExpression.op_bracketedPrimary, newCond);
            newCond = ASTFactory.newUnaryExpression(IASTUnaryExpression.op_not, newCond);
            newThenClause = newElseClause;
            newElseClause = null;
        }

        IASTIfStatement newif = ASTFactory.newIfStatement(newCond, newThenClause, newElseClause);
        return newif;
    }

    private List<IASTStatement> prepareStatements(IASTNode markedNode, List<IASTNode> optionalNodes, IASTStatement clause) {
        List<IASTStatement> statements = nodeListToCopiedStatements(getClauseStatementList(clause));
        IASTNode markedNodeInClause = findMarkedNodeInStatementList(markedNode, statements);
        if (markedNodeInClause != null) {
            statements.remove(markedNodeInClause);
        } else {
            statements.addAll(nodeListToCopiedStatements(optionalNodes));
        }
        return statements;
    }

    private IASTNode findMarkedNodeInStatementList(IASTNode markedNode, List<IASTStatement> thenStatements) {
        IASTGotoStatement[] gotoInNewList = new IASTGotoStatement[thenStatements.size()];
        int i = 0;
        for (IASTStatement iastStatement : thenStatements) {
            if (iastStatement instanceof IASTGotoStatement) gotoInNewList[i++] = (IASTGotoStatement) iastStatement;
        }
        IASTNode toDelete = findCorrespondingGoto(markedNode, getAllGotosInParent(markedNode.getParent()), gotoInNewList);
        return toDelete;
    }

    protected IASTNode findCorrespondingGoto(IASTNode searchNode, IASTGotoStatement[] oldGotoList, IASTGotoStatement[] newGotoList) {
        IASTNode newMarkedGoto = null;
        int i = 0;
        while (oldGotoList.length > i && newGotoList.length > i) {
            if (oldGotoList[i].equals(searchNode)) {
                newMarkedGoto = newGotoList[i];
            }
            i++;
        }
        return newMarkedGoto;
    }
}
