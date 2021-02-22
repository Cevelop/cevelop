package com.cevelop.codeanalysator.autosar.visitor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTEnumerationSpecifier;

import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.visitor.CodeAnalysatorVisitor;
import com.cevelop.codeanalysator.core.visitor.RuleReporter;


public class InitializeNoneFirstOrAllEnumeratorsVisitor extends CodeAnalysatorVisitor {

    public InitializeNoneFirstOrAllEnumeratorsVisitor(Rule rule, RuleReporter ruleReporter) {
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
            IASTEnumerator[] enumerators = enumerationSpecifier.getEnumerators();
            List<IASTEnumerator> initializedEnumerators = Arrays.stream(enumerators) //
                    .filter(enumerator -> enumerator.getValue() != null) //
                    .collect(Collectors.toList());
            if (initializedEnumerators.size() == 1 && initializedEnumerators.get(0) != enumerators[0] //
                || initializedEnumerators.size() > 1 && initializedEnumerators.size() != enumerators.length) {
                reportRuleForNode(enumerationSpecifier);
            }
        }
        return super.visit(declSpec);
    }

}
