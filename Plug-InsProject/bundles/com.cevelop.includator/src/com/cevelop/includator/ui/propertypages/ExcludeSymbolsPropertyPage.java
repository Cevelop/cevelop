package com.cevelop.includator.ui.propertypages;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.SymbolListEditor;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class ExcludeSymbolsPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

    IAdaptable                   element;
    private ResourceWrapperStore store;
    private SymbolListEditor     symbolListEditor;

    public ExcludeSymbolsPropertyPage() {
        store = new ResourceWrapperStore();
        setPreferenceStore(store);
        setDescription(
                "The symbols listed below are excluded from include analysis functions. Excluded symbols will not imply references to the corresponding declarations.");
    }

    @Override
    protected void createFieldEditors() {
        symbolListEditor = new SymbolListEditor(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, "List of excluded Symbols:",
                getFieldEditorParent());

        addField(symbolListEditor);
    }

    @Override
    public IAdaptable getElement() {
        return element;
    }

    @Override
    public void setElement(IAdaptable element) {
        this.element = element;
        ICProject project = getProject();
        String defaultValue = IncludatorPropertyManager.getWorkspacePreference(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE);
        store.setDefault(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, defaultValue);
        store.setResource(project.getProject());
    }

    private ICProject getProject() {
        IAdaptable element = getElement();
        if (element instanceof ICProject) {
            return (ICProject) element;
        } else if (element instanceof IProject) {
            IProject project = (IProject) element;
            return CoreModel.getDefault().create(project);
        }
        return null;
    }

    @Override
    protected void performDefaults() {
        // setting all values to null is required here, otherwise pressing ok after reset to defaults will not have an effect.
        store.setValue(IncludatorPropertyManager.P_EXCLUDE_SYMBOL_IN_WORKSPACE_PREFERENCE, null);
        super.performDefaults();
    }
}
