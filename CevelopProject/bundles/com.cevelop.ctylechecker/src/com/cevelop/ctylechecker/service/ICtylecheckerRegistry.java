package com.cevelop.ctylechecker.service;

public interface ICtylecheckerRegistry {

    IRuleService getRuleService();

    IStyleguideService getStyleguideService();

    IGroupingService getGroupingService();

    IConceptService getConceptService();

    IExpressionService getExpressionService();

    IConfigurationService getConfigurationService();
}
