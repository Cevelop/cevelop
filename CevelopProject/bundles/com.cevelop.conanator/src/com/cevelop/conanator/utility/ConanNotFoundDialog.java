package com.cevelop.conanator.utility;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.conanator.Activator;
import com.cevelop.conanator.preferences.PreferenceConstants;


public class ConanNotFoundDialog extends TitleAreaDialog {

    private IPath           conanPath;
    private FileFieldEditor conanPathEditor;
    private Button          saveInPrefsCheck;

    public ConanNotFoundDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        getShell().setText("Conan not found");
        setTitle("Unable to find Conan executable");
        validate();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout());

        Label messageLbl = new Label(container, SWT.WRAP);
        messageLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        messageLbl.setText("The Conan executable could neither be found at the location " + "specified in the preferences, nor in the PATH.\n" +
                           "Please provide the path in the below dialog.");

        createConanPathEditor(container);

        saveInPrefsCheck = new Button(container, SWT.CHECK);
        saveInPrefsCheck.setText("Save this path in the preferences");
        saveInPrefsCheck.setSelection(true);

        return area;
    }

    private void createConanPathEditor(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        conanPathEditor = new FileFieldEditor(PreferenceConstants.P_CONAN_PATH, "Path to Conan:", true, StringFieldEditor.VALIDATE_ON_KEY_STROKE,
                container);
        conanPathEditor.setEmptyStringAllowed(false);
        conanPathEditor.setPropertyChangeListener(event -> validate());
        conanPathEditor.setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    private void validate() {
        Button okBtn = getButton(IDialogConstants.OK_ID);

        if (conanPathEditor.isValid()) {
            okBtn.setEnabled(true);
            setErrorMessage(null);
        } else {
            okBtn.setEnabled(false);
            setErrorMessage(conanPathEditor.getErrorMessage());
        }
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void okPressed() {
        conanPath = new Path(conanPathEditor.getStringValue());
        savePathInPreferences();
        super.okPressed();
    }

    private void savePathInPreferences() {
        if (saveInPrefsCheck.getSelection()) {
            conanPathEditor.store();
        }
    }

    public IPath getConanPath() {
        return conanPath;
    }
}
