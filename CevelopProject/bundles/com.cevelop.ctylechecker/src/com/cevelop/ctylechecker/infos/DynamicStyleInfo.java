package com.cevelop.ctylechecker.infos;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;

import com.cevelop.ctylechecker.common.ResourceType;
import com.cevelop.ctylechecker.domain.IRule;
import com.cevelop.ctylechecker.service.util.ConfigurationMapper;


public class DynamicStyleInfo extends MarkerInfo<DynamicStyleInfo> {

    @InfoArgument
    public String ruleName = "";

    @InfoArgument
    public String ruleMessage = "";

    @InfoArgument
    public String jsonRule = "";

    @InfoArgument
    public ResourceType type = ResourceType.AST;

    public DynamicStyleInfo() {}

    public DynamicStyleInfo(IRule rule) {
        ruleName = rule.getName();
        ruleMessage = rule.getMessage();
        jsonRule = ConfigurationMapper.toJson(rule);
    }

    public DynamicStyleInfo(IRule rule, ResourceType type) {
        this(rule);
        this.type = type;
    }
}
