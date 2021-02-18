package com.cevelop.conanator.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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

import com.cevelop.conanator.models.ConanProfile;


public class ProfileDependencyListViewer {

    private Shell                shell;
    private ListViewer           viewer;
    private WritableList<String> model;
    private List<ConanProfile>   available;
    private Button               deleteButton;
    private ConanProfile         profile;

    public ProfileDependencyListViewer(Shell shell, Composite parent, ConanProfile profile, List<ConanProfile> available) {
        this.shell = shell;
        this.profile = profile;
        this.available = available;
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        model = new WritableList<>(profile.getDependencies(), ConanProfile.class);

        createViewer(container);
        createButtons(container);
    }

    private void createViewer(Composite parent) {
        viewer = new ListViewer(parent);
        viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        viewer.setContentProvider(new ObservableListContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        viewer.setInput(model);
        viewer.addSelectionChangedListener(e -> updateButtonStates());
    }

    private void createButtons(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));
        container.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

        createButton(container, "Add...", this::addDependency);
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

    protected void updateButtonStates() {
        deleteButton.setEnabled(!viewer.getSelection().isEmpty());
    }

    public void addDependency() {
        AddDependencyDialog dialog = new AddDependencyDialog(shell, profile, new ArrayList<>(available));
        if (dialog.open() == Window.OK) {
            model.add(dialog.getDependency());
        }
    }

    public void deleteSelected() {
        model.removeAll(viewer.getStructuredSelection().toList());
    }
}
