package com.cevelop.ctylechecker.domain;

import java.util.Collection;
import java.util.UUID;


public interface IStyleguide extends ICtyleElement {

    String GOOGLE    = "Google Styleguide";
    String CANONICAL = "Canonical Styleguide";
    String GEOSOFT   = "Geosoft Styleguide";

    void addRule(IRule pRule);

    IRule removeRule(UUID pId);

    IRule getRule(UUID pId);

    Collection<IRule> getRules();

    void addGrouping(IGrouping pGrouping);

    IGrouping removeGrouping(UUID pId);

    IGrouping getGrouping(UUID pId);

    Collection<IGrouping> getGroupings();

}
