package com.cevelop.includator.ui.preferencepages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.IndexedSubdirListEditor;
import com.cevelop.includator.ui.components.LblFlowingBooleanFieldEditor;


public class ExtendSymbolAwarenessPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ExtendSymbolAwarenessPreferencePage() {
        super(GRID);
        setPreferenceStore(IncludatorPlugin.getDefault().getPreferenceStore());
        setDescription(
                "Includator can gain awareness of symbols which are not (yet) included in a project, which is essential when writing new code. " +
                       "By default, all the headers located directly inside of all include-path-directories are considered. This action can be triggered by " +
                       "clicking 'Extend Symbol Awareness' in the Includator menu.");
    }

    @Override
    public void createFieldEditors() {
        Group settingsGroup = createSettingsGroup();

        String askAdaptIndexLabel = "Ask to adapt symbol awareness when running 'Organize Includes' if no yet done.";
        addFlowingBooleanField(IncludatorPropertyManager.ASK_TO_ADAPT_INDEX, askAdaptIndexLabel, settingsGroup);

        String warnAboutIgnoredExtensionLessFilesLabel =
                                                       "Warn about files without unknown type (files without file-extension) which are ignored during the process.";
        addFlowingBooleanField(IncludatorPropertyManager.WARN_ABOUT_IGNORED_EXTENSION_LESS_FILES, warnAboutIgnoredExtensionLessFilesLabel,
                settingsGroup);
        addListLabel();
        IndexedSubdirListEditor subDirEditor = new IndexedSubdirListEditor(IncludatorPropertyManager.PARSE_INDEX_UPFRONT_SUBDIRS_PREFERENCE_NAME,
                getFieldEditorParent());
        addField(subDirEditor);
    }

    private Group createSettingsGroup() {
        Group settingsGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        settingsGroup.setLayoutData(gridData);
        settingsGroup.setText("General Settings:");
        return settingsGroup;
    }

    private void addListLabel() {
        Label subDirListLabel = new Label(getFieldEditorParent(), SWT.WRAP);
        String subDirListDecription =
                                    "In special cases, awareness of additional symbols should also happen for subdirectories of include-path-directories. " +
                                      "The list below defines these subdirectories, which are taken as default for all C / C++ projects.";
        subDirListLabel.setText(subDirListDecription);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = 300;
        gridData.horizontalSpan = 2;
        subDirListLabel.setLayoutData(gridData);
    }

    private void addFlowingBooleanField(String name, String label, Composite parent) {
        addField(new LblFlowingBooleanFieldEditor(name, label, parent));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {}

}
