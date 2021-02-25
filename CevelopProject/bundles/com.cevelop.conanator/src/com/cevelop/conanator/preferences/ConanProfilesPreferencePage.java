package com.cevelop.conanator.preferences;

import java.io.File;
import java.util.Arrays;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.conanator.Activator;


public class ConanProfilesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Combo               profileCombo;
    private ProfileListViewer   viewer;
    private static final String PROFILES_FOLDER = System.getProperty("user.home") + "/.conan/profiles/";
    private static final String NONE_ENTRY      = "<none>";

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setLayout(new GridLayout());

        createDefaultProfileFieldEditor(content);

        viewer = new ProfileListViewer(getShell(), content);
        viewer.subscribe(event -> {
            String selected = getSelection();
            profileCombo.setItems(viewer.getProfileNames());
            profileCombo.add(NONE_ENTRY, 0);
            int id2 = getMatchingProfileIndex(selected);
            profileCombo.select(id2 >= 0 ? id2 : getDefaultProfileIndex());
        });
        return content;
    }

    private String getSelection() {
        int id = profileCombo.getSelectionIndex();
        if (id >= 0) {
            return profileCombo.getItem(id);
        }
        return null;
    }

    private void createDefaultProfileFieldEditor(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        content.setLayout(new GridLayout(2, false));

        Label profileLabel = new Label(content, SWT.NONE);
        profileLabel.setText("Default Conan Profile:");

        profileCombo = new Combo(content, SWT.DROP_DOWN | SWT.READ_ONLY);
        profileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        profileCombo.setItems(getProfiles());
        profileCombo.add(NONE_ENTRY, 0);
        profileCombo.select(getDefaultProfileIndex());
        profileCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.setDefaultProfile(getSelection());
            }
        });
    }

    private int getDefaultProfileIndex() {
        String defaultProfile = getPreferenceStore().getString(PreferenceConstants.P_WORKSPACE_PROFILE);
        return getMatchingProfileIndex(defaultProfile);
    }

    private int getMatchingProfileIndex(String profile) {
        for (int index = 0; index < profileCombo.getItemCount(); index++) {
            if (profileCombo.getItem(index).equals(profile)) {
                return index;
            }
        }

        return 0;
    }

    private String[] getProfiles() {
        File[] files = new File(PROFILES_FOLDER).listFiles();
        if (files == null) {
            return new String[0];
        }
        return Arrays.stream(files).map(File::getName).toArray(String[]::new);
    }

    @Override
    public boolean performOk() {
        viewer.performOk();

        String profile = profileCombo.getText();
        if (profile.equals(NONE_ENTRY)) {
            profile = "";
        }
        getPreferenceStore().setValue(PreferenceConstants.P_WORKSPACE_PROFILE, profile);

        return super.performOk();
    }

    @Override
    protected void performDefaults() {
        viewer.performDefaults();
        profileCombo.select(getDefaultProfileIndex());
    }

}
