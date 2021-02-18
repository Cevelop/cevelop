package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils.ES76GotoUsagePattern;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;


public class ES76_03AvoidGotoUseBreakQuickFix extends ES76_00AvoidGotoQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES76 + ": change to simple break";
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        final IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) return false;
        return ES76GotoUsagePattern.getPattern((IASTGotoStatement) markedNode) == ES76GotoUsagePattern.BREAK;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTLabelStatement label = getLabelStatement((IASTGotoStatement) markedNode);
        if (canRemoveLabel(label)) hRewrite.replace(label, label.getNestedStatement().copy(CopyStyle.withLocations), null);
        hRewrite.replace(markedNode, ASTFactory.newBreakStatement(), null);
    }
}
