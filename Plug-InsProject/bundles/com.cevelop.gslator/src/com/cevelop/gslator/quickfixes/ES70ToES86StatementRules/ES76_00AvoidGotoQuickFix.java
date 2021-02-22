package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.utils.ASTHelper;


abstract public class ES76_00AvoidGotoQuickFix extends BaseQuickFix {

    protected IASTLabelStatement getLabelStatement(IASTGotoStatement gotostmt) {
        IASTName[] refs = gotostmt.getTranslationUnit().getDeclarationsInAST(gotostmt.getName().resolveBinding());

        IASTLabelStatement label = null;
        for (IASTName iastName : refs) {
            if (iastName.getParent() instanceof IASTLabelStatement) {
                label = (IASTLabelStatement) iastName.getParent();
            }
        }
        return label;
    }

    protected boolean canRemoveLabel(IASTLabelStatement label) {
        return canRemoveLabel(label, 1);
    }

    protected boolean canRemoveLabel(IASTLabelStatement label, int removedGotos) {
        return label.getTranslationUnit().getReferences(label.getName().resolveBinding()).length == removedGotos;
    }

    protected IASTIfStatement getIfStatement(IASTNode markedNode) {
        IASTIfStatement parentIf = null;
        if (markedNode.getParent() instanceof IASTIfStatement) {
            parentIf = (IASTIfStatement) markedNode.getParent();
        } else { // no need to check because already checked in checker
            parentIf = (IASTIfStatement) markedNode.getParent().getParent();
        }
        return parentIf;
    }

    protected List<IASTNode> collectNodesBetween(IASTLabelStatement label, IASTIfStatement ifstmt) {
        List<IASTNode> optionalNodes = new ArrayList<>();
        IASTNode[] samelevelnodes = ifstmt.getParent().getChildren();
        boolean foundStartBorder = false;
        boolean foundEndBorder = false;
        for (IASTNode iastNode : samelevelnodes) {
            if (!foundStartBorder && (iastNode.equals(ifstmt) || iastNode.equals(label))) {
                foundStartBorder = true;
                if (iastNode.equals(label)) {
                    optionalNodes.add(label.getNestedStatement());
                }
                continue;
            }
            if (foundStartBorder && (iastNode.equals(ifstmt) || iastNode.equals(label))) {
                foundEndBorder = true;
            }

            if (foundStartBorder && !foundEndBorder) {
                optionalNodes.add(iastNode);
            }
        }
        return optionalNodes;
    }

    protected List<IASTStatement> nodeListToCopiedStatements(List<IASTNode> nodes) {
        List<IASTStatement> statements = new ArrayList<>();
        for (IASTNode iastNode : nodes) {
            statements.add((IASTStatement) iastNode.copy(CopyStyle.withLocations));
        }
        return statements;
    }

    protected List<IASTNode> getClauseStatementList(IASTStatement clause) {
        List<IASTNode> statements = new ArrayList<>();
        if (clause != null) {
            if (clause instanceof IASTCompoundStatement) {
                statements.addAll(Arrays.asList(clause.getChildren()));
            } else {
                statements.add(clause);
            }
        }
        return statements;
    }

    protected IASTGotoStatement[] getAllGotosInParent(IASTNode parent) {
        List<IASTGotoStatement> list = ASTHelper.findNodeTypes(parent, IASTGotoStatement.class);
        IASTGotoStatement[] returnArray = new IASTGotoStatement[list.size()];
        int i = 0;
        for (IASTGotoStatement iastGotoStatement : list) {
            returnArray[i++] = iastGotoStatement;
        }
        return returnArray;
    }

    protected List<IBinding> getAllLabelBindingsInParent(IASTNode parent) {
        List<IASTLabelStatement> list = ASTHelper.findNodeTypes(parent, IASTLabelStatement.class);
        List<IBinding> returnList = new ArrayList<>();
        for (IASTLabelStatement label : list) {
            returnList.add(label.getName().resolveBinding());
        }
        return returnList;
    }
}
