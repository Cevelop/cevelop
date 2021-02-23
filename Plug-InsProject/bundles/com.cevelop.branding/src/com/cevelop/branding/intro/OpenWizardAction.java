package com.cevelop.branding.intro;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.wizards.IWizardDescriptor;


public abstract class OpenWizardAction extends Action implements IIntroAction {

    public void openWizard(final String id) {

        IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
        IIntroPart intro = introManager.getIntro();
        introManager.setIntroStandby(intro, true);

        IWorkbench workbench = PlatformUI.getWorkbench();

        // First see if this is a "new wizard".
        IWizardDescriptor descriptor = workbench.getNewWizardRegistry().findWizard(id);
        // If not check if it is an "import wizard".
        if (descriptor == null) {
            descriptor = workbench.getImportWizardRegistry().findWizard(id);
        }
        // Or maybe an export wizard
        if (descriptor == null) {
            descriptor = workbench.getExportWizardRegistry().findWizard(id);
        }

        try {
            // Then if we have a wizard, open it.
            if (descriptor != null) {
                final IWorkbenchWizard wizard = descriptor.createWizard();
                wizard.init(workbench, null);
                WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
