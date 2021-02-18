package com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.gslator.utils.ASTHelper;


public enum ES76GotoUsagePattern {
    UNKNOWN, IF, LOOP, BREAK, LAMBDA;

    public static ES76GotoUsagePattern getPattern(IASTGotoStatement gotostmt) {
        IASTName[] refs = gotostmt.getTranslationUnit().getDeclarationsInAST(gotostmt.getName().resolveBinding());

        IASTLabelStatement label = null;
        for (IASTName iastName : refs) {
            if (iastName.getParent() instanceof IASTLabelStatement) label = (IASTLabelStatement) iastName.getParent();
        }

        if (label != null) {
            ES76GotoUsagePattern tmp = ifOrLoopBehaviourDetected(gotostmt, label);
            if (tmp != UNKNOWN) return tmp;
            return breakBehaviourDetected(gotostmt, label);
        }
        return UNKNOWN;
    }

    private static ES76GotoUsagePattern ifOrLoopBehaviourDetected(IASTGotoStatement gotostmt, IASTLabelStatement label) {
        if (gotoStatementIsOnlyStatementInIf(gotostmt) || gotoStatementIsLastStatementInIf(gotostmt)) {
            IASTIfStatement parentIf = getParentIf(gotostmt);

            if (parentIf.getParent().equals(label.getParent())) {
                IASTNode[] samelevelnodes = parentIf.getParent().getChildren();
                boolean foundIf = false;
                boolean foundLabel = false;
                for (IASTNode iastNode : samelevelnodes) {
                    if (iastNode.equals(parentIf)) foundIf = true;
                    if (iastNode.equals(label)) foundLabel = true;

                    // CASE: if behaviour
                    if (foundIf && !foundLabel) {
                        return IF;
                    }

                    // CASE: loop behaviour
                    if (foundLabel && !foundIf && gotoStatementIsOnlyStatementInIf(gotostmt) && gotoIsInThenPart(gotostmt)) {
                        return LOOP;
                    }
                }
            }
        }
        return UNKNOWN;
    }

    private static ES76GotoUsagePattern breakBehaviourDetected(IASTGotoStatement gotostmt, IASTLabelStatement label) {
        IASTNode innerParentLoop = ASTHelper.getNextOuterLoop(gotostmt);
        if (innerParentLoop != null) {
            IASTNode outerParentLoop = ASTHelper.getNextOuterLoop(innerParentLoop);

            if (outerParentLoop == null) {
                if (ASTHelper.isDirecltyAfterwards(innerParentLoop, label)) {
                    return BREAK;
                }
            } else {
                while (outerParentLoop != null) {
                    if (ASTHelper.isDirecltyAfterwards(outerParentLoop, label)) {
                        return LAMBDA;
                    }
                    outerParentLoop = ASTHelper.getNextOuterLoop(outerParentLoop);
                }
            }
        }
        return UNKNOWN;
    }

    private static IASTIfStatement getParentIf(IASTGotoStatement gotostmt) {
        IASTIfStatement parentIf = null;
        if (gotostmt.getParent() instanceof IASTIfStatement) {
            parentIf = (IASTIfStatement) gotostmt.getParent();
        } else {
            parentIf = (IASTIfStatement) gotostmt.getParent().getParent();
        }
        return parentIf;
    }

    private static boolean nodeIsInIf(IASTNode node) {
        return node.getParent() instanceof IASTIfStatement || (node.getParent() instanceof IASTCompoundStatement && node.getParent()
                .getParent() instanceof IASTIfStatement);
    }

    private static boolean gotoStatementIsLastStatementInIf(IASTGotoStatement gotostmt) {
        return nodeIsInIf(gotostmt) && ASTHelper.getNextNode(gotostmt) == null;
    }

    private static boolean gotoStatementIsOnlyStatementInIf(IASTGotoStatement gotostmt) {
        return nodeIsInIf(gotostmt) && (gotostmt.getParent() instanceof IASTIfStatement || gotostmt.getParent().getChildren().length == 1);
    }

    private static boolean gotoIsInThenPart(IASTGotoStatement gotostmt) {
        IASTIfStatement parentIf = getParentIf(gotostmt);
        IASTNode parent = gotostmt.getParent();
        return parentIf.getThenClause().equals(gotostmt) || parentIf.getThenClause().equals(parent);
    }
}
