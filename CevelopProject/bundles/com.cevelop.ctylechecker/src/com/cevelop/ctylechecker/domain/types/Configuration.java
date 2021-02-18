package com.cevelop.ctylechecker.domain.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IStyleguide;
import com.google.gson.annotations.Expose;


public class Configuration implements IConfiguration {

    @Expose
    private Map<UUID, IStyleguide> styleguides;
    @Expose
    private IStyleguide            activeStyleguide;
    @Expose
    private Boolean                enabled;
    private ConfigurationType      setting;

    public Configuration() {
        activeStyleguide = new Styleguide("");
        styleguides = new HashMap<>();
        setting = ConfigurationType.WORKSPACE;
        enabled = true;
    }

    @Override
    public Optional<IStyleguide> findStyleGuide(String pStyleguideName) {
        for (IStyleguide styleguide : styleguides.values()) {
            if (styleguide.getName().equals(pStyleguideName)) {
                return Optional.of(styleguide);
            }
        }
        return Optional.empty();
    }

    @Override
    public IStyleguide getActiveStyleguide() {
        return activeStyleguide;
    }

    @Override
    public void setActiveStyleguide(IStyleguide pNewActiveStyleguide) {
        activeStyleguide = pNewActiveStyleguide;
    }

    @Override
    public void addStyleguide(IStyleguide pNewStyleguide) {
        styleguides.put(pNewStyleguide.getId(), pNewStyleguide);
    }

    @Override
    public void addStyleguide(String pNewStyleguideName) {
        IStyleguide newStyleguide = new Styleguide(pNewStyleguideName);
        styleguides.put(newStyleguide.getId(), newStyleguide);
    }

    @Override
    public Collection<IStyleguide> getStyleguides() {
        return styleguides.values();
    }

    @Override
    public void setStyleguides(Collection<IStyleguide> pStyleguides) {
        Map<UUID, IStyleguide> newStyleguideMap = new HashMap<>();
        for (IStyleguide styleguide : pStyleguides) {
            newStyleguideMap.put(styleguide.getId(), styleguide);
        }
        this.styleguides = newStyleguideMap;
    }

    @Override
    public ConfigurationType getSetting() {
        return setting;
    }

    @Override
    public void setSetting(ConfigurationType pSetting) {
        this.setting = pSetting;
    }

    @Override
    public boolean isWorkspaceSetting() {
        return isSetting(ConfigurationType.WORKSPACE);
    }

    @Override
    public boolean isReferenceSetting() {
        return isSetting(ConfigurationType.REFERENCE);
    }

    @Override
    public boolean isProjectSetting() {
        return isSetting(ConfigurationType.PROJECT);
    }

    private boolean isSetting(ConfigurationType pSetting) {
        return setting == pSetting;
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public void isEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void removeStyleguide(IStyleguide styleguide) {
        styleguides.remove(styleguide.getId());
    }
}
