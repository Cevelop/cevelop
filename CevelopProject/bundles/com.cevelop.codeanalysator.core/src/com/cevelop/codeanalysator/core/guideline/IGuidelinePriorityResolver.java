package com.cevelop.codeanalysator.core.guideline;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public interface IGuidelinePriorityResolver {

    void registerSharedRule(Rule rule);

    void computePriorityOrderings();

    boolean isHighestActiveRuleForNode(Rule rule, IASTNode node);
}
