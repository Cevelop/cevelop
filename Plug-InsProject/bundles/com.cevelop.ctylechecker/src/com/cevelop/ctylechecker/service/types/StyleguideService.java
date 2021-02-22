package com.cevelop.ctylechecker.service.types;

import java.util.Optional;
import java.util.UUID;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.cevelop.ctylechecker.domain.types.Grouping;
import com.cevelop.ctylechecker.domain.types.Styleguide;
import com.cevelop.ctylechecker.service.IStyleguideService;
import com.cevelop.ctylechecker.service.util.StyleguideUtil;


public class StyleguideService implements IStyleguideService {

    @Override
    public IStyleguide createStyleguide(String pName) {
        return new Styleguide(pName);
    }

    @Override
    public Optional<IGrouping> findGroupOfRule(IStyleguide pStyleguide, IRule rule) {
        for (IGrouping grouping : pStyleguide.getGroupings()) {
            if (grouping.getRule(rule.getId()) != null) {
                return Optional.of(grouping);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<IGrouping> findGroupOfRule(IStyleguide pStyleguide, UUID pRuleId) {
        IRule rule = pStyleguide.getRule(pRuleId);
        if (rule != null) {
            return findGroupOfRule(pStyleguide, rule);
        }
        return Optional.empty();
    }

    @Override
    public IStyleguide makeCopy(IStyleguide pStyleguide) {
        Styleguide copy = new Styleguide(pStyleguide.getName());
        for (IGrouping grouping : pStyleguide.getGroupings()) {
            IGrouping copyGroup = new Grouping(grouping.getName(), grouping.isEnabled());
            for (IRule rule : grouping.getRules()) {
                copyGroup.addRule(StyleguideUtil.makeRuleCopy(rule));
            }
            copy.addGrouping(copyGroup);
        }
        for (IRule rule : pStyleguide.getRules()) {
            copy.addRule(StyleguideUtil.makeRuleCopy(rule));
        }
        return copy;
    }

}
