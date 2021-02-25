package com.cevelop.branding.intro;

import java.util.Properties;

import org.eclipse.ui.intro.IIntroSite;


public class NewProjectWizardAction extends OpenWizardAction {

    @Override
    public void run(IIntroSite site, Properties params) {
        openWizard("org.eclipse.cdt.ui.wizards.NewCWizard1");
    }
}
