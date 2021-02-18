package com.cevelop.charwars.commands;

import org.eclipse.cdt.codan.core.CodanCorePlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;


public class SettingsDialog extends Dialog {

    protected SettingsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        Button button = new Button(container, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        button.setText("Press me");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
                IFile file = workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
                IProject project = file.getProject();

                Preferences[] preferences = { new ProjectScope(project).getNode(CodanCorePlugin.PLUGIN_ID), InstanceScope.INSTANCE.getNode(
                        CodanCorePlugin.PLUGIN_ID), ConfigurationScope.INSTANCE.getNode(CodanCorePlugin.PLUGIN_ID), DefaultScope.INSTANCE.getNode(
                                CodanCorePlugin.PLUGIN_ID), };

                IPreferencesService prefService = Platform.getPreferencesService();

                String paramsKey = "com.cevelop.charwars.problems.CStringProblem.params";
                System.out.println(prefService.get(paramsKey, null, preferences));

                String severityKey = "com.cevelop.charwars.problems.CStringProblem";
                System.out.println(prefService.get(severityKey, null, preferences));
            }
        });

        return container;
    }

    // overriding this methods allows you to set the
    // title of the custom dialog
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("CharWars Settings");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(300, 500);
    }

}
