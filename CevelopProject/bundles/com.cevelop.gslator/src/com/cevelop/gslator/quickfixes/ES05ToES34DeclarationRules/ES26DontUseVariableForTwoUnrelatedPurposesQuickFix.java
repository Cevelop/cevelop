package com.cevelop.gslator.quickfixes.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;


public class ES26DontUseVariableForTwoUnrelatedPurposesQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        return Rule.ES26 + ": Quickfix";
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        // TODO: Handle marker...
    }
}
