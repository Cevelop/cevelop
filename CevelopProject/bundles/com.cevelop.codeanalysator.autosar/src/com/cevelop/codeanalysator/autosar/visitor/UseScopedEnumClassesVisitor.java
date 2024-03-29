package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTEnumerationSpecifier;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class UseScopedEnumClassesVisitor extends CodeAnalysatorVisitor {

    public UseScopedEnumClassesVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTEnumerationSpecifier) {
            ICPPASTEnumerationSpecifier enumerationSpecifier = (ICPPASTEnumerationSpecifier) declSpec;

            if (!enumerationSpecifier.isScoped()) {
                reportRuleForNode(enumerationSpecifier);
            }
        }
        return super.visit(declSpec);
    }

}
