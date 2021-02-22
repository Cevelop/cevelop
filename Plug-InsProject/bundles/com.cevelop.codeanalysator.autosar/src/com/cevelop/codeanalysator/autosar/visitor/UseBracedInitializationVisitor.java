package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;

import com.cevelop.codeanalysator.autosar.util.AutoHelper;
import com.cevelop.codeanalysator.autosar.util.BracedInitializationHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class UseBracedInitializationVisitor extends CodeAnalysatorVisitor {

    public UseBracedInitializationVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) decl;
            if (!AutoHelper.isAutoDeclaringVariables(simpleDeclaration)) {
                Arrays.stream(simpleDeclaration.getDeclarators()) //
                        .map(IASTDeclarator::getInitializer) //
                        .filter(Objects::nonNull) //
                        .filter(initializer -> !(initializer instanceof ICPPASTInitializerList)) //
                        .filter(initializer -> !BracedInitializationHelper.requiresParenthesisToCallConstructor(initializer)) //
                        .forEach(initializer -> reportRuleForNode(initializer));
            }
        }
        return super.visit(decl);
    }

}
