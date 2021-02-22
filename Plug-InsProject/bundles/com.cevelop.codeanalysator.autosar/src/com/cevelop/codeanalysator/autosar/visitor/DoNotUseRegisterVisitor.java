package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotUseRegisterVisitor extends CodeAnalysatorVisitor {

    public DoNotUseRegisterVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
        shouldVisitParameterDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpelDecl = (IASTSimpleDeclaration) decl;
            if (simpelDecl.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_register) {
                reportRuleForNode(decl);
            }
        }
        return super.visit(decl);
    }

    @Override
    public int visit(IASTParameterDeclaration paramDecl) {
        if (paramDecl.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_register) {
            reportRuleForNode(paramDecl);
        }
        return super.visit(paramDecl);
    }

}
