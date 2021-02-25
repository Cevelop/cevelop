package com.cevelop.codeanalysator.core.visitor;

import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.codeanalysator.core.guideline.IGuidelinePriorityResolver;
import com.cevelop.codeanalysator.core.guideline.Rule;


public class RuleReporter {

    private final AbstractIndexAstChecker    checker;
    private final IGuidelinePriorityResolver priorityResolver;

    public RuleReporter(AbstractIndexAstChecker checker, IGuidelinePriorityResolver priorityResolver) {
        this.checker = checker;
        this.priorityResolver = priorityResolver;
    }

    public void reportRuleForNode(Rule rule, IASTNode node, Object... args) {
        if (priorityResolver.isHighestActiveRuleForNode(rule, node)) {
            checker.reportProblem(rule.getProblemId(), node, args);
        }
    }

    public boolean isHighestPriorityRuleForNode(Rule rule, IASTNode node) {
        return priorityResolver.isHighestActiveRuleForNode(rule, node);
    }
}
