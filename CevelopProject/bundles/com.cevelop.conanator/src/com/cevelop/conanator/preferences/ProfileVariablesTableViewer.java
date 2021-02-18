package com.cevelop.conanator.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.conanator.models.Entry;


public class ProfileVariablesTableViewer {

    private WritableList<Entry> model;
    private TableViewer         viewer;
    private Button              deleteButton;

    public ProfileVariablesTableViewer(Composite parent, List<Entry> list) {
        Composite container = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        model = new WritableList<>(list, Entry.class);

        createViewer(container);
        createButtons(container);
    }

    private void createViewer(Composite parent) {
        viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

        List<ColumnData<Entry>> columns = new ArrayList<>();
        Predicate<Entry> canEdit = e -> e != null;
        columns.add(new ColumnData<>("Key", 150, entry -> entry.getKey(), (entry, key) -> entry.setKey(key), canEdit));
        columns.add(new ColumnData<>("Value", 250, entry -> entry.getValue(), (entry, value) -> entry.setValue(value), canEdit));
        ColumnBuilder.createColumns(viewer, columns);

        viewer.getTable().setHeaderVisible(true);
        viewer.setContentProvider(new ObservableListContentProvider());
        viewer.setInput(model);

        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addSelectionChangedListener(e -> updateButtonStates());
    }

    private void createButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = GridData.BEGINNING;
        container.setLayoutData(layoutData);

        createButton(container, "New", this::addNew);
        deleteButton = createButton(container, "Delete", this::deleteSelected);

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

    private void updateButtonStates() {
        deleteButton.setEnabled(!viewer.getSelection().isEmpty());
    }

    public void addNew() {
        model.add(new Entry("Var", "Val"));
    }

    public void deleteSelected() {
        model.removeAll(viewer.getStructuredSelection().toList());
    }
}
