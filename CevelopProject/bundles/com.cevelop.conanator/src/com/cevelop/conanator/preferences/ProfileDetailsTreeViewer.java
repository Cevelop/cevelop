package com.cevelop.conanator.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.conanator.models.ConanProfile;
import com.cevelop.conanator.models.Entry;
import com.cevelop.conanator.models.ProfileSection;
import com.cevelop.conanator.models.SectionEntry;


public class ProfileDetailsTreeViewer {

    private TreeViewer viewer;
    private Button     addButton;
    private Button     deleteButton;

    public ProfileDetailsTreeViewer(Composite parent, ConanProfile profile) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        createViewer(container, profile);
        createButtons(container);
    }

    public static void create(Composite parent, ConanProfile profile) {
        new ProfileDetailsTreeViewer(parent, profile);
    }

    private void createViewer(Composite container, ConanProfile profile) {
        viewer = new TreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        viewer.getTree().setHeaderVisible(true);
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        List<ColumnData<Object>> columns = new ArrayList<>();
        columns.add(new ColumnData<>(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return element.toString();
            }
        }, "Key", 150, entry -> entry instanceof SectionEntry ? ((Entry) entry).getKey() : null, (entry, key) -> {
            if (entry instanceof SectionEntry) {
                ((Entry) entry).setKey(key);
            }
        }, e -> e instanceof SectionEntry ? true : false));
        columns.add(new ColumnData<>(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return element instanceof SectionEntry ? ((SectionEntry) element).getValue() : null;
            }
        }, "Value", 250, entry -> entry instanceof SectionEntry ? ((Entry) entry).getValue() : null, (entry, value) -> {
            if (entry instanceof SectionEntry) {
                ((Entry) entry).setValue(value);
            }
        }, e -> e instanceof SectionEntry ? true : false));
        ColumnBuilder.createColumns(viewer, columns);

        viewer.setContentProvider(new ProfileContentProvider());
        viewer.setInput(profile);
        viewer.addSelectionChangedListener(e -> updateButtonStates());
    }

    private void createButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = GridData.BEGINNING;
        container.setLayoutData(layoutData);

        addButton = createButton(container, "New", this::addEntry);
        deleteButton = createButton(container, "Delete", this::deleteEntry);

        updateButtonStates();
    }

    private Button createButton(Composite container, String label, Runnable actionHandler) {
        Button button = new Button(container, SWT.PUSH);
        button.setText(label);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                actionHandler.run();
            }
        });
        return button;
    }

    protected void updateButtonStates() {
        addButton.setEnabled(!viewer.getSelection().isEmpty());
        ITreeSelection selection = viewer.getStructuredSelection();
        Object element = selection.getFirstElement();
        deleteButton.setEnabled(element != null && element instanceof SectionEntry);
    }

    private void addEntry() {
        IStructuredSelection selection = viewer.getStructuredSelection();
        Object element = selection.getFirstElement();
        ProfileSection section = null;
        if (element instanceof ProfileSection) {
            section = (ProfileSection) element;
        } else {
            section = ((SectionEntry) element).getSection();
        }
        SectionEntry entry = new SectionEntry(section, "Key", "Value");

        entry.getSection().getEntries().add(entry);
        viewer.refresh();
        viewer.setSelection(new StructuredSelection(entry), true);
    }

    private void deleteEntry() {
        IStructuredSelection selection = viewer.getStructuredSelection();
        Object element = selection.getFirstElement();
        if (element instanceof SectionEntry) {
            SectionEntry entry = (SectionEntry) element;
            entry.getSection().getEntries().remove(entry);
            viewer.refresh();
        }
    }

    private class ProfileContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getElements(Object inputElement) {
            return ((ConanProfile) inputElement).getSections().toArray();
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return ((ProfileSection) parentElement).getEntries().toArray();
        }

        @Override
        public Object getParent(Object element) {
            return element instanceof SectionEntry ? ((SectionEntry) element).getSection() : null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return element instanceof ProfileSection ? true : false;
        }

    }

}
