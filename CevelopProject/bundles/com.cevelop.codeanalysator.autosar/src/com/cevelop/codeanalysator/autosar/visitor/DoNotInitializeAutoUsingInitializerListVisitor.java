package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTEqualsInitializer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTInitializerList;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


@SuppressWarnings("restriction")
public class DoNotInitializeAutoUsingInitializerListVisitor extends CodeAnalysatorVisitor {

    public DoNotInitializeAutoUsingInitializerListVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if (violatesRule(declaration)) {
            String contextFlagsString = createContextFlagsString(declaration);
            reportRuleForNode(declaration, contextFlagsString);
        }
        return super.visit(declaration);
    }

    private boolean violatesRule(IASTDeclaration declaration) {
        if (!(declaration instanceof IASTSimpleDeclaration)) return false;
        IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
        if (!isAutoDeclaration(simpleDeclaration)) return false;
        return hasInitializerList(simpleDeclaration);
    }

    private boolean hasInitializerList(IASTSimpleDeclaration simpleDeclaration) {
        IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
        for (IASTDeclarator declarator : declarators) {
            if (hasInitializerList(declarator)) return true;
        }
        return false;
    }

    private boolean hasInitializerList(IASTDeclarator declarator) {
        IASTInitializer initializer = declarator.getInitializer();
        if (initializer instanceof CPPASTInitializerList) return true;
        if (!(initializer instanceof CPPASTEqualsInitializer)) return false;
        CPPASTEqualsInitializer equalsInitializer = (CPPASTEqualsInitializer) initializer;
        IASTInitializerClause initClause = equalsInitializer.getInitializerClause();
        return (initClause instanceof CPPASTInitializerList);
    }

    private boolean isAutoDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        IASTDeclSpecifier declSpec = simpleDeclaration.getDeclSpecifier();
        if (!(declSpec instanceof ICPPASTSimpleDeclSpecifier)) return false;
        ICPPASTSimpleDeclSpecifier simpleDeclSpec = (ICPPASTSimpleDeclSpecifier) declSpec;
        int type = simpleDeclSpec.getType();
        return (type == IASTSimpleDeclSpecifier.t_auto || type == IASTSimpleDeclSpecifier.t_decltype_auto);
    }

    private String createContextFlagsString(IASTDeclaration declaration) {
        if (!(declaration instanceof IASTSimpleDeclaration)) return "";
        IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
        return isControlDeclaration(simpleDeclaration) ? ContextFlagsHelper.UseAutoSparinglyContextFlagControlDeclaration : "";
    }

    private boolean isControlDeclaration(IASTSimpleDeclaration simpleDeclaration) {
        IASTNode parent = simpleDeclaration.getParent();
        if (parent instanceof IASTIfStatement) {
            return true;
        } else if (parent instanceof IASTSwitchStatement) {
            return true;
        } else if (parent instanceof IASTWhileStatement) {
            return true;
        } else if (parent instanceof IASTForStatement) {
            return true;
        } else if (parent instanceof ICPPASTRangeBasedForStatement) {
            return true;
        }
        return false;
    }
}
