package com.cevelop.ctylechecker.domain;

import java.util.Collection;
import java.util.Optional;


public interface IConfiguration {

    Optional<IStyleguide> findStyleGuide(String pStyleguideName);

    IStyleguide getActiveStyleguide();

    void setActiveStyleguide(IStyleguide pNewActiveStyleguide);

    void addStyleguide(IStyleguide pNewStyleguide);

    void addStyleguide(String pNewStyleguideName);

    Collection<IStyleguide> getStyleguides();

    void setStyleguides(Collection<IStyleguide> pStyleguides);

    ConfigurationType getSetting();

    void setSetting(ConfigurationType pSetting);

    boolean isWorkspaceSetting();

    boolean isReferenceSetting();

    boolean isProjectSetting();

    void removeStyleguide(IStyleguide styleguide);

    void isEnabled(Boolean enabled);

    Boolean isEnabled();

}
