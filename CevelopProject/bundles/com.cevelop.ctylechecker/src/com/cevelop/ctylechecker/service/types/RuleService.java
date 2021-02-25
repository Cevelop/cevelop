package com.cevelop.ctylechecker.service.types;

import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.types.Rule;
import com.cevelop.ctylechecker.service.IRuleService;


public class RuleService implements IRuleService {

    @Override
    public IRule createRule(String pName, Boolean pEnabled) {
        IRule newRule = new Rule(pName, pEnabled);
        return newRule;
    }

    @Override
    public IRule updateRule(IRule pSource, IRule pTarget) {
        pSource.setName(pTarget.getName());
        pSource.setMessage(pTarget.getMessage());
        pSource.setPredefinedExpressions(pTarget.getPredefinedExpressions());
        pSource.setCustomExpressions(pTarget.getCustomExpressions());
        pSource.isEnabled(pTarget.isEnabled());
        pSource.setCheckedConcepts(pTarget.getCheckedConcepts());
        return pSource;
    }

}
