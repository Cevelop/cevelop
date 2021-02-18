package com.cevelop.ctylechecker.ui.preferences;

import java.util.Optional;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com.cevelop.ctylechecker.domain.ConfigurationType;
import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.component.SettingsComposite;


public class CtylecheckerPropertiesPage extends PropertyPage implements IWorkbenchPropertyPage {

    private IConfiguration        config;
    private IConfigurationService configService = CtylecheckerRuntime.getInstance().getRegistry().getConfigurationService();
    private SettingsComposite     settingsComposite;

    @Override
    protected Control createContents(Composite parent) {
        loadConfiguration();
        settingsComposite = new SettingsComposite(parent, config, this);
        return settingsComposite;
    }

    private void loadConfiguration() {
        Optional<IEclipsePreferences> oPrefs = configService.getProjectPreferencesFromAdaptable(getElement());
        if (oPrefs.isPresent()) {
            config = configService.loadConfiguration(oPrefs.get());
        } else {
            CtylecheckerRuntime.showMessage(Messages.INFO, Messages.FAILED_CONFIG_LOAD);
            config = configService.getDefaultConfiguration();
        }
    }

    public IConfiguration loadConfigurationCallback(ConfigurationType pSetting) {
        Optional<IEclipsePreferences> oPrefs = configService.getProjectPreferencesFromAdaptable(getElement());
        if (oPrefs.isPresent()) {
            config = configService.loadConfiguration(oPrefs.get(), pSetting);
        } else {
            CtylecheckerRuntime.showMessage(Messages.INFO, Messages.FAILED_CONFIG_LOAD);
            config = configService.getDefaultConfiguration();
        }
        return config;
    }

    @Override
    protected void performDefaults() {
        configService.restoreDefaults(config);
        if (settingsComposite != null) {
            settingsComposite.refreshActiveStyleguideCombo();
            settingsComposite.refreshGroupingsAndRulesTree();
        }
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        performSave();
        return super.performOk();
    }

    @Override
    protected void performApply() {
        performSave();
        super.performApply();
    }

    private void performSave() {
        Optional<IEclipsePreferences> oPrefs = configService.getProjectPreferencesFromAdaptable(getElement());
        if (oPrefs.isPresent()) {
            IEclipsePreferences prefs = oPrefs.get();
            configService.saveConfiguration(prefs, config);
            try {
                prefs.flush();
                tryInitCodanChecking();
            } catch (BackingStoreException e) {
                e.printStackTrace();
                CtylecheckerRuntime.showMessage(Messages.INFO, Messages.FAILED_SAVE_TRAY_AGAIN);
            }
        } else {
            showProjectNotFoundMessage();
        }
    }

    private void tryInitCodanChecking() {
        Optional<IFile> oFile = configService.getActiveFile();
        if (oFile.isPresent()) {
            CodanRuntime.getInstance().getBuilder().processResource(oFile.get(), new NullProgressMonitor());
        }
    }

    private void showProjectNotFoundMessage() {
        CtylecheckerRuntime.showMessage(Messages.INFO, Messages.FAILED_PROJECT_LOAD);
    }
}
