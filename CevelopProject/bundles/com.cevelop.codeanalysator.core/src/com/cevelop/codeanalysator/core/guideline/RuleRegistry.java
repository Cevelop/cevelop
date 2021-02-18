package com.cevelop.codeanalysator.core.guideline;

import java.util.HashMap;


public class RuleRegistry {

    private HashMap<String, Rule>      rulesById = new HashMap<String, Rule>();
    private IGuidelinePriorityResolver priorityResolver;

    public RuleRegistry(IGuidelinePriorityResolver priorityResolver) {
        this.priorityResolver = priorityResolver;
    }

    public Rule getRuleByProblemId(String problemId) {
        Rule rule = rulesById.get(problemId);
        if (rule == null) throw new IllegalStateException(String.format("no rule with this problemId registered. [%s]", problemId));
        return rule;
    }

    public Rule createAndRegisterRule(String ruleNr, IGuideline guideline, String problemId) {
        Rule rule = new Rule(ruleNr, guideline, problemId);
        registerRule(rule);
        return rule;
    }

    public Rule createAndRegisterRule(String ruleNr, IGuideline guideline, String problemId, String sharedProblemId) {
        Rule rule = new Rule(ruleNr, guideline, problemId, sharedProblemId);
        registerRule(rule);
        priorityResolver.registerSharedRule(rule);
        return rule;
    }

    private void registerRule(Rule rule) {
        Rule previousRule = rulesById.putIfAbsent(rule.getProblemId(), rule);
        if (previousRule != null) throw new IllegalArgumentException(String.format("rule with this problemId registered. [%s]", rule.getProblemId()));
    }
}
