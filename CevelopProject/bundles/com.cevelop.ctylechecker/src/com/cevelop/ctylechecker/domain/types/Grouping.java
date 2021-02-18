package com.cevelop.ctylechecker.domain.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.google.gson.annotations.Expose;


public class Grouping extends AbstractCtyleElement implements IGrouping {

    @Expose
    private Map<UUID, IRule> rules;

    public Grouping(String pGroupingName) {
        this(pGroupingName, true);
    }

    public Grouping(String pGroupingName, Boolean pEnabled) {
        super(pGroupingName, pEnabled);
        rules = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IGrouping#addRule(com.cevelop.ctylechecker.domain.IRule)
     */
    @Override
    public void addRule(IRule pRule) {
        rules.put(pRule.getId(), pRule);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IGrouping#removeRule(java.util.UUID)
     */
    @Override
    public IRule removeRule(UUID pId) {
        return rules.remove(pId);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IGrouping#getRules()
     */
    @Override
    public Collection<IRule> getRules() {
        return rules.values();
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IGrouping#getRule(java.util.UUID)
     */
    @Override
    public IRule getRule(UUID pId) {
        return rules.get(pId);
    }
}
