package com.cevelop.includator.ui.propertypages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;


class ProjectResourceEditor extends ListEditor {

    private final IProject project;

    ProjectResourceEditor(IProject project, String name, String labelText, Composite parent, boolean showAddButton) {
        super(name, labelText, parent);
        this.project = project;
        customizeButtons(showAddButton);
    }

    private void customizeButtons(boolean showAddButton) {
        final Button addButton = getAddButton();
        if (showAddButton) {
            addButton.setText("Add...");
            addButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    ArrayList<String> selectedItems = getNewInputObjects();
                    addItems(selectedItems);
                }
            });
        }
        addButton.setEnabled(showAddButton);
        getUpButton().setVisible(false);
        getDownButton().setVisible(false);
    }

    @Override
    protected String[] parseString(String stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new String[0];
        }
        return stringList.split(",\\s*");
    }

    public void addItems(Collection<String> items) {
        List list = getList();
        if (items != null && items.size() > 0) {
            list.setItems(items.toArray(new String[0]));
            selectionChanged();
        }
    }

    protected ArrayList<String> getNewInputObjects() {

        final WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();
        final BaseWorkbenchContentProvider contentProvider = new BaseWorkbenchContentProvider();
        final ArrayList<String> resultItems = new ArrayList<>();
        final ArrayList<String> selectedContainers = new ArrayList<>();
        CheckedTreeSelectionDialog dlg = createProjectResourceSelectionDialog(labelProvider, contentProvider);

        ICProject cProject = CoreModel.getDefault().create(project).getCProject();
        dlg.setInput(cProject);
        if (dlg.open() == IDialogConstants.OK_ID) {
            mergeSelection(resultItems, selectedContainers, dlg);
        }
        return resultItems;
    }

    private void mergeSelection(final ArrayList<String> resultItems, final ArrayList<String> selectedContainers, CheckedTreeSelectionDialog dlg) {
        for (Object item : dlg.getResult()) {
            if (item instanceof ICContainer) {
                final String itemPath = ((ICContainer) item).getResource().getProjectRelativePath().toOSString();
                if (!isSubOfSelected(itemPath, selectedContainers)) {
                    selectedContainers.add(itemPath);
                    resultItems.add(itemPath);
                }
            } else if (item instanceof ICElement) {
                final String itemPath = ((ICElement) item).getResource().getProjectRelativePath().toOSString();
                if (!isSubOfSelected(itemPath, selectedContainers)) {
                    resultItems.add(itemPath);
                }
            }
        }

        for (String item : getList().getItems()) {
            if (!isSubOfSelected(item, selectedContainers)) {
                resultItems.add(item);
            }
        }
    }

    private boolean isSubOfSelected(String itemPath, ArrayList<String> selectedContainers) {
        for (String container : selectedContainers) {
            if (itemPath.startsWith(container)) {
                return true;
            }
        }
        return false;
    }

    // Suppress original add button behavior
    @Override
    protected String getNewInputObject() {
        return null;
    }

    private CheckedTreeSelectionDialog createProjectResourceSelectionDialog(final WorkbenchLabelProvider labelProvider,
            final BaseWorkbenchContentProvider contentProvider) {
        final Class<?>[] acceptedTypes = new Class<?>[] { ISourceRoot.class, ICContainer.class, ITranslationUnit.class };
        CheckedTreeSelectionDialog dlg = new CheckedTreeSelectionDialog(getShell(), labelProvider, contentProvider);

        dlg.setTitle("Exclusion Selection");
        dlg.setMessage("Select elements to be excluded:");

        dlg.setContainerMode(false);
        dlg.setComparator(new ViewerComparator() {

            @Override
            public int category(Object element) {
                for (int i = 0; i < acceptedTypes.length; i++) {
                    if (acceptedTypes[i].isInstance(element)) {
                        return i;
                    }
                }
                return acceptedTypes.length;
            }
        });
        dlg.addFilter(new TypeFilter(acceptedTypes));
        List listItems = getList();
        dlg.addFilter(new SelectedFilter(listItems.getItems()));
        return dlg;
    }

    @Override
    protected String createList(String[] items) {
        String string = Arrays.toString(items);
        return string.substring(1, string.length() - 1);
    }
}
