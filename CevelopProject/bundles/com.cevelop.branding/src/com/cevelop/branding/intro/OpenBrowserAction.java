package com.cevelop.branding.intro;

import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;


public class OpenBrowserAction extends Action implements IIntroAction {

    @Override
    public void run(IIntroSite site, Properties params) {
        String url = (String) params.get("url");
        Program.launch(url);
    }
}
