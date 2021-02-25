package com.cevelop.branding.intro;

import java.util.Properties;

import org.eclipse.ui.intro.IIntroSite;


public class NewCuteSuiteWizardAction extends OpenWizardAction {

    @Override
    public void run(IIntroSite site, Properties params) {
        openWizard("cute_plugin.createSuiteWizard");
    }
}
