package com.cevelop.conanator.preferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.cevelop.conanator.Activator;
import com.cevelop.conanator.models.Remote;
import com.cevelop.conanator.utility.OrderedSectionParser;


public class RemoteTableViewer {

    private Shell                parentShell;
    private OrderedSectionParser parser;
    private List<Remote>         defaultInput    = new ArrayList<>();
    private WritableList<Remote> currentInput;
    private TableViewer          viewer;
    private Table                table;
    private Button               editBtn;
    private Button               removeBtn;
    private Button               upBtn;
    private Button               downBtn;
    private boolean              presentsDefault = true;

    public RemoteTableViewer(Shell parentShell, Composite parent) {
        this.parentShell = parentShell;

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);

        createTableViewer(container);
        createButtonBox(container);
    }

    private void createTableViewer(Composite parent) {
        viewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

        createColumns();

        table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.addSelectionChangedListener(e -> updateButtonStates());

        currentInput = getRemotesFromFile();
        ViewerSupport.bind(viewer, currentInput, BeanProperties.values("name", "url", "ssl"));
        for (Remote r : currentInput) {
            defaultInput.add(r.clone());
        }
    }

    private void createColumns() {
        String[] titles = { "Remote Name", "Remote URL", "Verify SSL" };
        int[] widths = { 150, 250, 100 };

        for (int columnId = 0; columnId < titles.length; columnId++) {
            createColumn(titles[columnId], widths[columnId]);
        }
    }

    private void createColumn(String title, int width) {
        TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);

        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(width);
        column.setResizable(true);
        column.setMoveable(true);
    }

    public void createButtonBox(Composite parent) {
        Composite buttonBox = new Composite(parent, SWT.NULL);
        buttonBox.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        buttonBox.setLayout(layout);

        createButtons(buttonBox);
        updateButtonStates();
    }

    private void createButtons(Composite parent) {
        createButton(parent, "Add...", this::addPressed);
        editBtn = createButton(parent, "Edit...", this::editPressed);
        removeBtn = createButton(parent, "Remove", this::removePressed);
        upBtn = createButton(parent, "Up", this::upPressed);
        downBtn = createButton(parent, "Down", this::downPressed);
    }

    private Button createButton(Composite parent, String text, Runnable selectionHandler) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        button.setText(text);

        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                selectionHandler.run();
            }
        });

        return button;
    }

    private void addPressed() {
        EditRemoteDialog dialog = new EditRemoteDialog(parentShell, currentInput);

        if (dialog.open() == Window.OK) {
            Remote remote = dialog.getRemote();
            int index = table.getSelectionIndex() + 1;

            currentInput.add(index, remote);
            viewer.setSelection(new StructuredSelection(remote));

            presentsDefault = false;
            updateButtonStates();
        }
    }

    private void editPressed() {
        int index = table.getSelectionIndex();

        if (index >= 0) {
            EditRemoteDialog dialog = new EditRemoteDialog(parentShell, currentInput, currentInput.get(index));

            if (dialog.open() == Window.OK) {
                presentsDefault = false;
                updateButtonStates();
            }
        }
    }

    private void removePressed() {
        int index = table.getSelectionIndex();

        if (index >= 0) {
            currentInput.remove(index);

            if (!currentInput.isEmpty()) {
                viewer.setSelection(new StructuredSelection(currentInput.get(index >= currentInput.size() ? index - 1 : index)));
            }

            presentsDefault = false;
            updateButtonStates();
        }
    }

    private void upPressed() {
        swap(true);
    }

    private void downPressed() {
        swap(false);
    }

    private void swap(boolean up) {
        int selectedIndex = table.getSelectionIndex();
        int targetIndex = up ? selectedIndex - 1 : selectedIndex + 1;

        if (selectedIndex >= 0) {
            Remote remote = currentInput.remove(selectedIndex);
            currentInput.add(targetIndex, remote);
            viewer.setSelection(new StructuredSelection(remote));

            presentsDefault = false;
            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        int index = table.getSelectionIndex();
        int size = currentInput.size();

        editBtn.setEnabled(index >= 0);
        removeBtn.setEnabled(index >= 0);
        upBtn.setEnabled(size > 1 && index > 0);
        downBtn.setEnabled(size > 1 && index >= 0 && index < size - 1);
    }

    public void save() throws IOException {
        if (presentsDefault) {
            return;
        }

        List<String> remotesSection = currentInput.stream().map(Remote::toString).collect(Collectors.toList());
        parser.setSection(0, remotesSection);
        parser.save();

        defaultInput = new ArrayList<>();
        for (Remote r : currentInput) {
            defaultInput.add(r.clone());
        }
        presentsDefault = true;
    }

    public void loadDefault() {
        if (presentsDefault) {
            return;
        }

        currentInput.clear();
        for (Remote r : defaultInput) {
            currentInput.add(r.clone());
        }

        presentsDefault = true;
        updateButtonStates();
    }

    private WritableList<Remote> getRemotesFromFile() {
        WritableList<Remote> remotes = new WritableList<>();
        File remotesFile = new File(System.getProperty("user.home") + "/.conan/registry.txt");

        try {
            if (!remotesFile.exists()) {
                if (!remotesFile.getParentFile().exists()) {
                    remotesFile.getParentFile().mkdirs();
                }
                remotesFile.createNewFile();
            }

            parser = new OrderedSectionParser(remotesFile);
        } catch (IOException e) {
            Activator.log(e);
            return remotes;
        }

        List<String> remotesSection = parser.getSection(0);

        for (String line : remotesSection) {
            String[] parts = line.split(" ");
            if (parts.length == 3) {
                remotes.add(new Remote(parts[0], parts[1], parts[2]));
            }
        }

        return remotes;
    }
}
