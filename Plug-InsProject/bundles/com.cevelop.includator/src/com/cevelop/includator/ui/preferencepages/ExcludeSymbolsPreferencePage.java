package com.cevelop.includator.ui.preferencepages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.SymbolListEditor;


public class ExcludeSymbolsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ExcludeSymbolsPreferencePage() {
        super(GRID);
        setPreferenceStore(IncludatorPlugin.getDefault().getPreferenceStore());
        setDescription("Symbols to exclude during include analysis:");
    }

    @Override
    public void createFieldEditors() {
        addField(new SymbolListEditor(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "Excluded Symbols",
                getFieldEditorParent()));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {}
}
