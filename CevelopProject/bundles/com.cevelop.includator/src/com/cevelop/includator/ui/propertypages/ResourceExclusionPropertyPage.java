package com.cevelop.includator.ui.propertypages;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class ResourceExclusionPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

    IAdaptable                         element;
    private final ResourceWrapperStore store;
    private ListEditor                 excludeResourceEditor;

    public ResourceExclusionPropertyPage() {
        super(GRID);
        store = new ResourceWrapperStore();
        setPreferenceStore(store);
        setDescription("The resources listed below are excluded from project- and folder-wide analysis functions:");
    }

    @Override
    public void createFieldEditors() {
        String editorName = IncludatorPropertyManager.EXCLUDE_RESOURCES_PROPERTY_NAME;
        excludeResourceEditor = new ProjectResourceEditor(getProject(), editorName, "List of excluded resources:", getFieldEditorParent(), true);
        addField(excludeResourceEditor);

    }

    @Override
    public IAdaptable getElement() {
        return element;
    }

    @Override
    public void setElement(IAdaptable element) {
        this.element = element;
        store.setDefault(IncludatorPropertyManager.EXCLUDE_RESOURCES_PROPERTY_NAME, "");
        store.setResource(getProject());
    }

    private IProject getProject() {
        IAdaptable element = getElement();
        if (element instanceof ICProject) {
            return ((ICProject) element).getProject();
        } else if (element instanceof IProject) {
            return (IProject) element;
        }
        return null;
    }

    @Override
    protected void performDefaults() {
        // setting all values to null is required here, otherwise pressing ok after reset to defaults will not have an effect.
        store.setValue(IncludatorPropertyManager.EXCLUDE_RESOURCES_PROPERTY_NAME, null);
        super.performDefaults();
    }
}
