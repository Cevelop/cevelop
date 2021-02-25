package com.cevelop.conanator.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.conanator.Activator;


public class ConanPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private FileFieldEditor   conanPathEditor;
    private RemoteTableViewer remoteTableViewer;

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setLayout(new GridLayout());

        createConanPathEditor(content);

        Label separator = new Label(content, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createRemoteTableViewer(content);

        validate();

        return content;
    }

    private void createConanPathEditor(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        conanPathEditor = new FileFieldEditor(PreferenceConstants.P_CONAN_PATH, "Path to Conan:", true, StringFieldEditor.VALIDATE_ON_KEY_STROKE,
                container);
        conanPathEditor.setPropertyChangeListener(event -> validate());
        conanPathEditor.setPage(this);
        conanPathEditor.setPreferenceStore(getPreferenceStore());
        conanPathEditor.load();
    }

    private void createRemoteTableViewer(Composite parent) {
        Label remoteLbl = new Label(parent, SWT.NONE);
        remoteLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        remoteLbl.setText("Remotes:");

        remoteTableViewer = new RemoteTableViewer(getShell(), parent);
    }

    private void validate() {
        setValid(conanPathEditor.isValid());
    }

    @Override
    protected void performDefaults() {
        conanPathEditor.loadDefault();
        remoteTableViewer.loadDefault();
    }

    @Override
    public boolean performOk() {
        try {
            remoteTableViewer.save();
        } catch (IOException e) {
            Activator.log(e);
            return false;
        }

        conanPathEditor.store();

        return super.performOk();
    }
}
