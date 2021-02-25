package com.cevelop.ctylechecker.service;

import com.cevelop.ctylechecker.domain.IRule;


public interface IRuleService {

    IRule createRule(String pName, Boolean pEnabled);

    IRule updateRule(IRule pSource, IRule pTarget);
}
