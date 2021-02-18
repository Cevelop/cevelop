package com.cevelop.gslator.quickfixes.ES70ToES86StatementRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression.CaptureDefault;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.gslator.charwarsstub.asttools.ASTModifier;
import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.utils.ES76GotoUsagePattern;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class ES76_04AvoidGotoUseLambdaReturnQuickFix extends ES76_00AvoidGotoQuickFix {

    @Override
    public boolean isApplicable(final IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        final IASTNode markedNode = getMarkedNode(marker);
        if (markedNode == null) return false;
        if (ES76GotoUsagePattern.getPattern((IASTGotoStatement) markedNode) != ES76GotoUsagePattern.LAMBDA) return false;

        IASTLabelStatement label = getLabelStatement((IASTGotoStatement) markedNode);
        IASTNode oldLoop = findParentLoopInFrontOfLabel(markedNode, label);

        IASTGotoStatement[] gotos = getAllGotosInParent(oldLoop);
        List<IBinding> bindings = getAllLabelBindingsInParent(oldLoop);

        for (IASTGotoStatement iastGotoStatement : gotos) {
            if (!iastGotoStatement.getName().resolveBinding().equals(label.getName().resolveBinding())) {
                if (!bindings.contains(iastGotoStatement.getName().resolveBinding())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getLabel() {
        return Rule.ES76 + ": put loops inside lambda function and use return";
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        IASTLabelStatement label = getLabelStatement((IASTGotoStatement) markedNode);
        IASTNode oldLoop = findParentLoopInFrontOfLabel(markedNode, label);

        IASTNode newLoop = oldLoop.copy(CopyStyle.withLocations);

        IASTGotoStatement[] gotos = getAllGotosInParent(newLoop);
        int removedGotos = 0;
        for (IASTGotoStatement iastGotoStatement : gotos) {
            if (iastGotoStatement.getName().toString().equals(label.getName().toString())) {
                ASTModifier.replaceNode(iastGotoStatement, ASTFactory.newReturnStatement(null));
                removedGotos++;
            }
        }

        List<IASTStatement> lambdacontent = new ArrayList<>();
        lambdacontent.add((IASTStatement) newLoop);

        IASTExpressionStatement newLambda = ASTFactory.newExpressionStatement(ASTFactory.newFunctionCallExpression(ASTFactory.newLambdaExpression(
                ASTFactory.newCompoundStatement(lambdacontent), CaptureDefault.BY_REFERENCE), null));

        hRewrite.replace(oldLoop, newLambda, null);

        if (canRemoveLabel(label, removedGotos)) {
            hRewrite.replace(label, label.getNestedStatement().copy(CopyStyle.withLocations), null);
        }
    }

    private IASTNode findParentLoopInFrontOfLabel(IASTNode markedNode, IASTLabelStatement label) {
        IASTNode loop = ASTHelper.getNextOuterLoop(markedNode);
        while (loop != null) {
            if (ASTHelper.isDirecltyAfterwards(loop, label)) break;
            loop = ASTHelper.getNextOuterLoop(loop);
        }
        return loop;
    }
}
