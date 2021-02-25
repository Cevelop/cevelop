package com.cevelop.ctylechecker.domain.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;


public class Styleguide extends AbstractCtyleElement implements IStyleguide {

    @Expose
    private Map<UUID, IGrouping> groupings;
    @Expose
    private Map<UUID, IRule>     rules;

    public Styleguide(String pStyleguideName) {
        super(pStyleguideName, true);
        groupings = new HashMap<>();
        rules = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#addRule(com.cevelop.ctylechecker.domain.IRule)
     */
    @Override
    public void addRule(IRule pRule) {
        rules.put(pRule.getId(), pRule);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#removeRule(java.util.UUID)
     */
    @Override
    public IRule removeRule(UUID pId) {
        IRule rule = rules.remove(pId);
        if (rule == null) {
            for (IGrouping grouping : groupings.values()) {
                rule = grouping.removeRule(pId);
                if (rule != null) {
                    return rule;
                }
            }
        }
        return rule;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#getRule(java.util.UUID)
     */
    @Override
    public IRule getRule(UUID pId) {
        IRule rule = rules.get(pId);
        if (rule == null) {
            for (IGrouping grouping : groupings.values()) {
                rule = grouping.getRule(pId);
                if (rule != null) {
                    return rule;
                }
            }
        }
        return rule;
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#getRules()
     */
    @Override
    public Collection<IRule> getRules() {
        return rules.values();
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#addGrouping(com.cevelop.ctylechecker.domain.Grouping)
     */
    @Override
    public void addGrouping(IGrouping pGrouping) {
        groupings.put(pGrouping.getId(), pGrouping);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#removeGrouping(java.util.UUID)
     */
    @Override
    public IGrouping removeGrouping(UUID pId) {
        return groupings.remove(pId);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#getGrouping(java.util.UUID)
     */
    @Override
    public IGrouping getGrouping(UUID pId) {
        return groupings.get(pId);
    }

    /*
     * (non-Javadoc)
     * @see com.cevelop.ctylechecker.domain.IStyleguide#getGroupings()
     */
    @Override
    public Collection<IGrouping> getGroupings() {
        return groupings.values();
    }
}
