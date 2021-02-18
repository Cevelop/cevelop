package com.cevelop.ctylechecker.domain;

import java.util.Collection;
import java.util.UUID;


public interface IGrouping extends ICtyleElement {

    void addRule(IRule pRule);

    IRule removeRule(UUID pId);

    Collection<IRule> getRules();

    IRule getRule(UUID pId);
}
