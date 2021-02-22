package com.cevelop.codeanalysator.core.visitor;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.codeanalysator.core.guideline.Rule;


public abstract class CodeAnalysatorVisitor extends ASTVisitor {

    private final Rule         rule;
    private final RuleReporter ruleReporter;

    protected CodeAnalysatorVisitor(Rule rule, RuleReporter ruleReporter) {
        this.ruleReporter = ruleReporter;
        this.rule = rule;
        setShouldVisit();
    }

    protected abstract void setShouldVisit();

    public Rule getRule() {
        return rule;
    }

    protected boolean isRuleSuppressedForNode(IASTNode node) {
        return rule.isSuppressedForNode(node);
    }

    protected void reportRuleForNode(IASTNode node, Object... args) {
        ruleReporter.reportRuleForNode(rule, node, args);
    }

    protected boolean isHighestPriorityRuleForNode(IASTNode node) {
        return ruleReporter.isHighestPriorityRuleForNode(rule, node);
    }
}
