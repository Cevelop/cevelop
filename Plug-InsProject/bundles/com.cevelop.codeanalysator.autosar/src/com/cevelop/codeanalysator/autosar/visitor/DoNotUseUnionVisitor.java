package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class DoNotUseUnionVisitor extends CodeAnalysatorVisitor {

    public DoNotUseUnionVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(IASTDeclaration decl) {
        if (decl instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration sd = (IASTSimpleDeclaration) decl;
            IASTDeclSpecifier declSpec = sd.getDeclSpecifier();
            if (declSpec instanceof IASTCompositeTypeSpecifier) {
                IASTCompositeTypeSpecifier compositeTypeSpec = (IASTCompositeTypeSpecifier) declSpec;
                if (compositeTypeSpec.getKey() == IASTCompositeTypeSpecifier.k_union) {
                    reportRuleForNode(decl);
                }
            }
        }

        return super.visit(decl);
    }
}
