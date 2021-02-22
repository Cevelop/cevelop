package com.cevelop.gslator.checkers.C40ToC52ConstructorRules;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTWhileStatement;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.C40ToC52ConstructorRules.C49NoAssignmentsInCtorVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.nodes.util.AttributeOwnerHelper;


public class C49NoAssignmentsInCtorChecker extends BaseChecker {

    public static final String IGNORE_STRING = "Rc-initialize";

    @Override
    public void checkAst(final IASTTranslationUnit ast) {
        ast.accept(new C49NoAssignmentsInCtorVisitor(this));
    }

    @Override
    public Rule getRule() {
        return Rule.C49;
    }

    @Override
    public ProblemId getProblemId() {
        return ProblemId.P_C49;
    }

    @Override
    public String getIgnoreString() {
        return IGNORE_STRING;
    }

    @Override
    public IASTNode getIgnoreAttributeNode(IASTNode markedNode) {
        IASTNode grandParent = markedNode.getParent().getParent();
        if (grandParent instanceof ICPPASTForStatement || grandParent instanceof ICPPASTWhileStatement) {
            return grandParent;
        }

        return AttributeOwnerHelper.getWantedAttributeOwner(markedNode);
    }
}
