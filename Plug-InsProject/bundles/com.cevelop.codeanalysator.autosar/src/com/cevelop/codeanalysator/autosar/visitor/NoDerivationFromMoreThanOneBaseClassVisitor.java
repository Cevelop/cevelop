package com.cevelop.codeanalysator.autosar.visitor;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;

import com.cevelop.codeanalysator.autosar.util.InterfaceHelper;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class NoDerivationFromMoreThanOneBaseClassVisitor extends CodeAnalysatorVisitor {

    public NoDerivationFromMoreThanOneBaseClassVisitor(Rule rule, RuleReporter ruleReporter) {
        super(rule, ruleReporter);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        if (violatesRule(declSpec)) {
            reportRuleForNode(declSpec);
        }
        return super.visit(declSpec);
    }

    private boolean violatesRule(IASTDeclSpecifier declSpec) {
        if (!(declSpec instanceof ICPPASTCompositeTypeSpecifier)) {
            return false;
        }
        ICPPASTCompositeTypeSpecifier typeSpec = (ICPPASTCompositeTypeSpecifier) declSpec;
        ICPPASTBaseSpecifier[] baseSpecs = typeSpec.getBaseSpecifiers();

        int amountOfBaseClasses = 0;
        for (ICPPASTBaseSpecifier baseSpec : baseSpecs) {
            if (!InterfaceHelper.isInterface(baseSpec)) {
                if (++amountOfBaseClasses > 1) {
                    return true;
                }
            }
        }

        return false;
    }
}
