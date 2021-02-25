package com.cevelop.ctylechecker.service;

import java.util.Optional;
import java.util.UUID;

import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.domain.IStyleguide;


public interface IStyleguideService {

    IStyleguide createStyleguide(String pName);

    Optional<IGrouping> findGroupOfRule(IStyleguide pStyleguide, UUID pRuleId);

    Optional<IGrouping> findGroupOfRule(IStyleguide pStyleguide, IRule rule);

    IStyleguide makeCopy(IStyleguide pStyleguide);
}
