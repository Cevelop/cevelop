package com.cevelop.includator.ui.propertypages;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.ui.IWorkbenchPropertyPage;

import com.cevelop.includator.preferences.IncludatorPropertyManager;
import com.cevelop.includator.ui.components.IndexedSubdirListEditor;
import com.cevelop.includator.ui.helpers.ResourceWrapperStore;


public class AdaptSymbolAwarenessPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

    private IAdaptable                 element;
    private final ResourceWrapperStore store;
    private ListEditor                 subDirEditor;

    public AdaptSymbolAwarenessPropertyPage() {
        super(GRID);
        store = new ResourceWrapperStore();
        setPreferenceStore(store);
        setDescription(
                "Includator can extend its awareness about symbols so non-included headers can be found ('Extend Symbol Awareness' in the Includator menu). " +
                       "By default, all the headers located directly inside of all include-path-directories are considered. " +
                       "In special cases, this should also happen for subdirectories of include-path-directories (like 'boost', 'sys', etc). The list below defines these subdirectories for the current project.");
    }

    @Override
    public void createFieldEditors() {
        subDirEditor = new IndexedSubdirListEditor(IncludatorPropertyManager.PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME, getFieldEditorParent());
        addField(subDirEditor);
    }

    @Override
    public IAdaptable getElement() {
        return element;
    }

    @Override
    public void setElement(IAdaptable element) {
        this.element = element;
        ICProject project = getProject();
        store.setDefault(IncludatorPropertyManager.PARSE_INDEX_UPFRONT_SUBDIRS_PROPERTY_NAME, IncludatorPropertyManager
                .getParseIndexUpfrontSubdirsDefaultProperty(project));
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
        ICProject project = getProject();
        if (project != null) {
            IncludatorPropertyManager.initParseIndexUpfrontSubdirsProjectProperty(project);
            subDirEditor.loadDefault();
        }
    }
}
