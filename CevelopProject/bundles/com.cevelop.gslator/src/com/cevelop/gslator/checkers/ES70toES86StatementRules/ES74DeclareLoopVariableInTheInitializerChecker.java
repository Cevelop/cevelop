package com.cevelop.gslator.checkers.ES70toES86StatementRules;

import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.ES70ToES86StatementRules.ES74DeclareLoopVariableInTheIntializerVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;


public class ES74DeclareLoopVariableInTheInitializerChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Res-for-init";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new ES74DeclareLoopVariableInTheIntializerVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.ES74;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_ES74;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    @Override
    public IASTNode getIgnoreAttributeNode(IASTNode markedNode) {
        IASTNode node = markedNode;
        while (!(node instanceof IASTForStatement) && !(node instanceof IASTTranslationUnit)) {
            node = node.getParent();
        }
        if (node instanceof IASTForStatement) {
            return node;
        }
        return null;
    }
}
