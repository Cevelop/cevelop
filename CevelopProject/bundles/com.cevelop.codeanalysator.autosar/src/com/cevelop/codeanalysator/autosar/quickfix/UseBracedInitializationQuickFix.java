package com.cevelop.codeanalysator.autosar.quickfix;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.codeanalysator.autosar.util.BracedInitializationHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class UseBracedInitializationQuickFix extends BaseQuickFix {

    public UseBracedInitializationQuickFix(String label) {
        super(label);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (markedNode instanceof IASTInitializer) {
            IASTInitializer initializer = (IASTInitializer) markedNode;
            if (!BracedInitializationHelper.requiresParenthesisToCallConstructor(initializer)) {
                replaceWithBracedInitialization(initializer, hRewrite);
            }
        }
    }

    private void replaceWithBracedInitialization(IASTInitializer nonBracedInitializer, ASTRewrite hRewrite) {
        IASTInitializerList bracedInitializer = createBracedFromNonBracedInitializer(nonBracedInitializer);
        hRewrite.replace(nonBracedInitializer, bracedInitializer, null);
    }

    private IASTInitializerList createBracedFromNonBracedInitializer(IASTInitializer initializer) {
        IASTInitializerList bracedInitializer = factory.newInitializerList();
        if (initializer instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer parenthesesInitializer = (ICPPASTConstructorInitializer) initializer;
            Arrays.stream(parenthesesInitializer.getArguments()) //
                    .map(IASTInitializerClause::copy) //
                    .forEach(bracedInitializer::addClause);
        } else if (initializer instanceof IASTEqualsInitializer) {
            IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            IASTInitializerClause equalsInitializerClause = equalsInitializer.getInitializerClause();
            if (equalsInitializerClause instanceof IASTInitializerList) {
                return ((IASTInitializerList) equalsInitializerClause).copy();
            } else if (equalsInitializerClause != null) {
                bracedInitializer.addClause(equalsInitializerClause.copy());
            }
        }
        return bracedInitializer;
    }
}
