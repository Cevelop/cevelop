package com.cevelop.ctylechecker.ui.preferences;

import java.util.Optional;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.service.IConfigurationService;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.component.SettingsComposite;


public class CtylecheckerPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

    public CtylecheckerPreferencesPage() {}

    private IConfiguration        config;
    private IConfigurationService configService = CtylecheckerRuntime.getInstance().getRegistry().getConfigurationService();
    private SettingsComposite     settingsComposite;

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(CtylecheckerRuntime.getPreferenceStore());
        config = configService.loadConfiguration(getPreferenceStore());
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
        configService.saveConfiguration(getPreferenceStore(), config);
        tryInitCodanChecking();
    }

    private void tryInitCodanChecking() {
        Optional<IFile> oFile = configService.getActiveFile();
        if (oFile.isPresent()) {
            CodanRuntime.getInstance().getBuilder().processResource(oFile.get(), new NullProgressMonitor());
        }
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
    protected Control createContents(Composite parent) {
        settingsComposite = new SettingsComposite(parent, config);
        return settingsComposite;
    }
}
