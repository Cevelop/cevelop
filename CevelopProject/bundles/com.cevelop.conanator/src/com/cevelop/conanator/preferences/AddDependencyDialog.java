package com.cevelop.conanator.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.conanator.models.ConanProfile;


public class AddDependencyDialog extends TitleAreaDialog {

    private ConanProfile       profile;
    private String             dependency;
    private Combo              profileCombo;
    private List<ConanProfile> available;

    public AddDependencyDialog(Shell parentShell, ConanProfile profile, List<ConanProfile> available) {
        super(parentShell);
        this.profile = profile;
        this.available = available;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = (Composite) super.createDialogArea(parent);
        content.setLayout(new GridLayout());
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createDependencyCombo(content);
        return content;
    }

    private void createDependencyCombo(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        content.setLayout(new GridLayout(2, false));

        Label profileLabel = new Label(content, SWT.NONE);
        profileLabel.setText("Select dependency:");

        profileCombo = new Combo(content, SWT.DROP_DOWN | SWT.READ_ONLY);
        profileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        profileCombo.setItems(getProfiles());
        profileCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                String profileName = profileCombo.getItem(profileCombo.getSelectionIndex());
                dependency = profileName;
                checkCycle();
            }
        });
    }

    private ConanProfile getProfileFromName(String name) {
        for (ConanProfile p : available) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    protected void checkCycle() {
        Set<String> visited = new HashSet<>();
        visited.add(profile.getName());
        boolean hasCycle = checkCycle(dependency, visited);
        getButton(IDialogConstants.OK_ID).setEnabled(!hasCycle);;
        if (hasCycle)
            setMessage("The selected profile creates a dependency cycle", IMessageProvider.WARNING);
        else setMessage("");
    }

    private boolean checkCycle(String profileName, Set<String> visited) {
        if (visited.contains(profileName)) return true;
        visited.add(profileName);
        boolean hasCycle = false;
        ConanProfile profile = getProfileFromName(profileName);
        for (String p : profile.getDependencies())
            hasCycle = hasCycle || checkCycle(p, visited);
        return hasCycle;
    }

    private String[] getProfiles() {
        List<String> comboList = new ArrayList<>();
        for (ConanProfile p : available) {
            comboList.add(p.getName());
        }
        comboList.remove(profile.getName());
        comboList.removeAll(profile.getDependencies());
        return comboList.toArray(new String[0]);
    }

    public String getDependency() {
        return dependency;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Add Dependency");
        shell.setSize(400, 200);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
}
