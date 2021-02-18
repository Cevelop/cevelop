package com.cevelop.conanator.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.conanator.models.ConanProfile;


public class EditProfileDialog extends Dialog {

    private ConanProfile       profile;
    private Shell              parentShell;
    private List<ConanProfile> profileList;

    public EditProfileDialog(Shell parentShell, ConanProfile profile, final List<ConanProfile> profileList) {
        super(parentShell);
        this.parentShell = parentShell;
        this.profile = profile;
        this.profileList = profileList;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = (Composite) super.createDialogArea(parent);
        content.setLayout(new GridLayout());
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label dependenciesLabel = new Label(content, SWT.NONE);
        dependenciesLabel.setText("Dependencies");
        new ProfileDependencyListViewer(parentShell, content, profile, profileList);

        Label variablesLabel = new Label(content, SWT.NONE);
        variablesLabel.setText("Variables");
        new ProfileVariablesTableViewer(content, profile.getVariables());

        Label settingsLabel = new Label(content, SWT.NONE);
        settingsLabel.setText("Settings");
        new ProfileDetailsTreeViewer(content, profile);

        Label buildRequiresLabel = new Label(content, SWT.NONE);
        buildRequiresLabel.setText("Build Requirements");
        new ProfileBuildRequiresTableViewer(parentShell, content, profile.getBuildRequires());
        return content;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit Profile - " + profile.getName());
        shell.setMinimumSize(400, 500);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
}
