package com.cevelop.conanator.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.conanator.models.Line;


public class ProfileBuildRequiresTableViewer {

    private TableViewer        viewer;
    private WritableList<Line> model;
    private Button             deleteButton;
    private Shell              shell;

    public ProfileBuildRequiresTableViewer(Shell shell, Composite parent, List<Line> list) {
        this.shell = shell;
        Composite container = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        model = new WritableList<>(list, String.class);

        createViewer(container);
        createButtons(container);
    }

    private void createViewer(Composite parent) {
        viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

        List<ColumnData<Line>> columns = new ArrayList<>();
        Predicate<Line> canEdit = e -> e != null;
        columns.add(new ColumnData<>("", 400, entry -> entry.toString(), (entry, value) -> entry.setValue(value), canEdit));
        ColumnBuilder.createColumns(viewer, columns);

        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setContentProvider(new ObservableListContentProvider());
        viewer.setInput(model);
        viewer.addSelectionChangedListener(e -> updateButtonStates());
    }

    private void createButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = GridData.BEGINNING;
        container.setLayoutData(layoutData);

        createButton(container, "New...", this::addNew);
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

    private void addNew() {
        InputDialog dialog = new InputDialog(shell, "Add New Build Requirement", "Please enter the build requirement.\n", "requirement", result -> {
            String errorMsg = null;
            if (result.isEmpty()) {
                errorMsg = "May not be empty.";
            }
            if (contains(result)) {
                errorMsg = "List already contains this element.";
            }
            return errorMsg;
        });
        if (dialog.open() == Window.OK) {
            model.add(new Line(dialog.getValue()));
        }
    }

    private boolean contains(String result) {
        for (Line line : model) {
            if (line.getValue().equals(result)) return true;
        }
        return false;
    }

    private void deleteSelected() {
        model.removeAll(viewer.getStructuredSelection().toList());
    }
}
