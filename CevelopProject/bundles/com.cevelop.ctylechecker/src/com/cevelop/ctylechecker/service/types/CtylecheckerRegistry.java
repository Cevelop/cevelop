package com.cevelop.ctylechecker.service.types;

import com.cevelop.ctylechecker.service.IConceptService;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.ICtylecheckerRegistry;
import com.cevelop.ctylechecker.service.IExpressionService;
import com.cevelop.ctylechecker.service.IGroupingService;
import com.cevelop.ctylechecker.service.IRuleService;
import com.cevelop.ctylechecker.service.IStyleguideService;


public class CtylecheckerRegistry implements ICtylecheckerRegistry {

    private IRuleService          ruleService;
    private IStyleguideService    styleguideService;
    private IGroupingService      groupingService;
    private IConceptService       conceptService;
    private IExpressionService    expressionService;
    private IConfigurationService configurationService;

    public CtylecheckerRegistry() {
        ruleService = new RuleService();
        styleguideService = new StyleguideService();
        groupingService = new GroupingService();
        conceptService = new ConceptService();
        expressionService = new ExpressionService();
        configurationService = new ConfigurationService();
    }

    public IRuleService getRuleService() {
        return ruleService;
    }

    @Override
    public IStyleguideService getStyleguideService() {
        return styleguideService;
    }

    @Override
    public IGroupingService getGroupingService() {
        return groupingService;
    }

    @Override
    public IConceptService getConceptService() {
        return conceptService;
    }

    @Override
    public IExpressionService getExpressionService() {
        return expressionService;
    }

    @Override
    public IConfigurationService getConfigurationService() {
        return configurationService;
    }
}
