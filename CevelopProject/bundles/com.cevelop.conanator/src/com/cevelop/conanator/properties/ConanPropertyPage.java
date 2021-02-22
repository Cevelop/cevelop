package com.cevelop.conanator.properties;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com.cevelop.conanator.Activator;
import com.cevelop.conanator.preferences.PreferenceConstants;


public class ConanPropertyPage extends PropertyPage {

    private static final String PROFILES_FOLDER = System.getProperty("user.home") + "/.conan/profiles/";
    private static final String NONE_ENTRY      = "<none>";

    private IEclipsePreferences preferenceNode;
    private Button              enableProjectSettingsCheck;
    private Label               profileLbl;
    private Combo               profileCombo;

    public ConanPropertyPage() {
        super();
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        loadPreferenceNode();

        Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        content.setLayout(new GridLayout());

        Composite firstSection = createTwoColumnContainer(content);
        createEnableProjectSettingsCheck(firstSection);
        createConfigWorkspaceSettingsLink(firstSection);

        Label separator = new Label(content, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite secondSection = createTwoColumnContainer(content);
        createProfileCombo(secondSection);

        updateSettingsEnabledState();

        return content;
    }

    private void loadPreferenceNode() {
        String errorMsg = null;
        IResource resource = getElement().getAdapter(IResource.class);

        if (resource == null) {
            errorMsg = "Unable to get project associated with selection";
        } else if ((preferenceNode = new ProjectScope(resource.getProject()).getNode(Activator.PLUGIN_ID)) == null) {
            errorMsg = "Unable to get preference node to load/store settings";
        }

        if (errorMsg != null) {
            setErrorMessage(errorMsg);
            setValid(false);
        }
    }

    private void createEnableProjectSettingsCheck(Composite parent) {
        enableProjectSettingsCheck = new Button(parent, SWT.CHECK);
        enableProjectSettingsCheck.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        enableProjectSettingsCheck.setText("Enable project-specific settings");
        enableProjectSettingsCheck.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateSettingsEnabledState();
            }
        });

        if (preferenceNode != null) {
            enableProjectSettingsCheck.setSelection(preferenceNode.getBoolean(PreferenceConstants.P_ENABLE_PROJECT_SETTINGS, false));
        }
    }

    private void createConfigWorkspaceSettingsLink(Composite parent) {
        Link configWorkspaceSettingsLink = new Link(parent, SWT.NONE);
        configWorkspaceSettingsLink.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
        configWorkspaceSettingsLink.setText("<a>Configure Workspace Settings...</a>");
        configWorkspaceSettingsLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String prefPageId = "com.cevelop.conanator.preferences.ConanProfilePreferencePage";
                PreferencesUtil.createPreferenceDialogOn(getShell(), prefPageId, new String[] { prefPageId }, null).open();

                reloadProfiles();
            }
        });
    }

    private void createProfileCombo(Composite parent) {
        profileLbl = new Label(parent, SWT.NONE);
        profileLbl.setText("Active Conan Profile:");

        profileCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        profileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        reloadProfiles();
    }

    private Composite createTwoColumnContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);

        return container;
    }

    private void updateSettingsEnabledState() {
        boolean enabled = enableProjectSettingsCheck.getSelection();
        profileLbl.setEnabled(enabled);
        profileCombo.setEnabled(enabled);
    }

    private void reloadProfiles() {
        String activeProfile = profileCombo.getText();

        if (activeProfile.isEmpty() && preferenceNode != null) {
            activeProfile = preferenceNode.get(PreferenceConstants.P_PROJECT_PROFILE, "");
        }

        profileCombo.setItems(getProfiles());
        profileCombo.add(NONE_ENTRY, 0);
        profileCombo.select(getMatchingProfileIndex(activeProfile));
    }

    private String[] getProfiles() {
        File[] files = new File(PROFILES_FOLDER).listFiles();
        if (files == null) {
            return new String[0];
        }
        return Arrays.stream(files).map(File::getName).toArray(String[]::new);
    }

    private int getMatchingProfileIndex(String profile) {
        for (int index = 0; index < profileCombo.getItemCount(); index++) {
            if (profileCombo.getItem(index).equals(profile)) {
                return index;
            }
        }

        return 0;
    }

    @Override
    protected void performDefaults() {
        profileCombo.select(0);
        enableProjectSettingsCheck.setSelection(false);
        updateSettingsEnabledState();

        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        if (preferenceNode != null) {
            boolean enableProjectSettings = enableProjectSettingsCheck.getSelection();
            String projectProfile = profileCombo.getText();

            preferenceNode.putBoolean(PreferenceConstants.P_ENABLE_PROJECT_SETTINGS, enableProjectSettings);

            if (enableProjectSettings && !projectProfile.equals(NONE_ENTRY)) {
                preferenceNode.put(PreferenceConstants.P_PROJECT_PROFILE, projectProfile);
            } else {
                preferenceNode.remove(PreferenceConstants.P_PROJECT_PROFILE);
            }

            try {
                preferenceNode.flush();
            } catch (BackingStoreException e) {
                Activator.log(e);
            }
        }

        return super.performOk();
    }
}
