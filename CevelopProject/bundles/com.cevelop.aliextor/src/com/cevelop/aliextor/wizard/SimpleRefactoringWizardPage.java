package com.cevelop.aliextor.wizard;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.wizard.helper.Pair;
import com.cevelop.aliextor.wizard.helper.TemplateNameHelper;


public class SimpleRefactoringWizardPage extends BaseWizardPage {

    private static final String TEXT_RADIO_SELECTED = "Just refactor selected";
    private static final String TEXT_RADIO_ALL      = "Refactor all";

    private Button   rbtnJustSelected;
    protected Button rbtnRefactorAll;
    private Button   cbxCreateTemplateAlias;

    private AliExtorRefactoring refactoring;

    private Table     selectionTable;
    private Composite tableComposite;
    private Label     aliasLabel;

    private ArrayList<Pair<String, Integer>> selectedNames;
    private TemplateNameHelper               helper;

    public SimpleRefactoringWizardPage(String name) {
        super(name);
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);

        rbtnJustSelected = createRadioButton(rbtnJustSelected, TEXT_RADIO_SELECTED, SWT.RADIO);
        rbtnJustSelected.setSelection(true);

        // Somehow the focus has to be set after the setSelection
        theUserInput.setFocus();

        rbtnRefactorAll = createRadioButton(rbtnRefactorAll, TEXT_RADIO_ALL, SWT.RADIO);

        // Dummy Labels to create an empty line
        createDummyLabel();
        createDummyLabel();

        if (ASTHelper.selectedNodeHasTemplateId(((AliExtorRefactoring) getRefactoring()).getIRefactoringSelection())) {
            refactoring = (AliExtorRefactoring) getRefactoring();
            helper = new TemplateNameHelper(refactoring.getNames(), refactoring.getSelectedNodeForTemplateAlias());
            tableComposite = new Composite(baseContainer, SWT.NONE);
            tableComposite.setLayout(createPageLayout());
            tableComposite.setLayoutData(createPageLayoutData());

            createComponents();
        }

        setSelectionInRefactoring(null);

        // required to avoid an error in the system
        setControl(baseContainer);
        setPageComplete(false);
    }

    protected void createComponents() {
        setMessage("Template parameters available!");
        addInformationLabel();
        addSelectionTable();
        addPreviewLabels();
        selectionTable.setSelection(0);
    }

    protected Layout createPageLayout() {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        return layout;
    }

    protected Object createPageLayoutData() {
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        return layoutData;
    }

    private void addPreviewLabels() {
        new Label(tableComposite, SWT.FILL | SWT.WRAP).setText("Type:\n\t" + refactoring.getSelectedNodeForTemplateAlias());
        aliasLabel = new Label(tableComposite, SWT.FILL | SWT.WRAP);
        // Couldn't fix that
        aliasLabel.setText(helper.getAliasTemplate(selectedNames, getTheUserInput()) + dummyString());
    }

    private String dummyString() {
        return "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
               "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    }

    private void addInformationLabel() {
        Label label = new Label(tableComposite, SWT.FILL | SWT.WRAP);
        final GridData data = new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1);
        label.setLayoutData(data);
        label.setText("Choose the the Types, which should be refactored into a Template Alias. The Parameters name starts with T1 to Tx");
        // label.setText(
        // "Choose the the Types, which should be refactored into a Template
        // Alias. The Parameters name starts with T1 to Tx");
    }

    private void addSelectionTable() {
        selectionTable = new Table(tableComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FILL);
        selectionTable.setLayoutData(createTableLayoutData());
        selectionTable.setLinesVisible(true);
        selectionTable.setHeaderVisible(true);
        selectionTable.addMouseListener(new CellSelectionListener());
        addColumnHeadings();
        helper.addTableValues(selectionTable);
    }

    private Object createTableLayoutData() {
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.grabExcessVerticalSpace = true;
        layoutData.minimumHeight = 250;
        return layoutData;
    }

    private void addColumnHeadings() {
        selectionTable.setHeaderVisible(true);
        for (int i = 0; i < getHeadings().length; ++i) {
            TableColumn column = new TableColumn(selectionTable, SWT.NULL);
            column.setResizable(false);
            column.setText(getHeadings()[i]);
            System.out.println("KEVIN " + getHeadings()[i]);
            column.pack();
        }
    }

    protected String[] getHeadings() {
        return new String[] { "", "Type before extraction\t\t", "Parameter of\t\t", "Name after extraction\t\t" };
    }

    private void setSelectedNamesInRefactoring() {
        refactoring.setSelectedNamesInRefactoring(selectedNames);
    }

    private class CellSelectionListener extends MouseAdapter {

        public CellSelectionListener() {
            selectedNames = new ArrayList<>();
        }

        @Override
        public void mouseUp(MouseEvent e) {
            TableItem item = findEventItem(createPoint(e));
            if (item == null) {
                return;
            }
            helper.setItemsNew(selectionTable, item);
            setSelectedNames();
            setAliasLabelNew();
            setNameAfterRefactoringNew();
            setSelectedNamesInRefactoring();
        }

        private void setNameAfterRefactoringNew() {
            int i = 1;
            for (TableItem item : selectionTable.getItems()) {
                if (item.getChecked()) {
                    item.setText(3, selectedNames.size() > 1 ? "T" + i++ : "T");
                }
            }
        }

        private void setAliasLabelNew() {
            aliasLabel.setText(helper.getAliasTemplate(selectedNames, getTheUserInput()));
        }

        private void setSelectedNames() {
            selectedNames.clear();
            for (TableItem item : selectionTable.getItems()) {
                if (item.getChecked()) {
                    selectedNames.add(getData(item));
                }
            }
        }

        @SuppressWarnings("unchecked")
        private Pair<String, Integer> getData(TableItem item) {
            return ((Pair<Pair<String, Integer>, ArrayList<Pair<String, Integer>>>) item.getData()).getLeft();
        }

        private TableItem findEventItem(Point point) {
            return selectionTable.getItem(point);
        }

        private Point createPoint(MouseEvent e) {
            return new Point(e.x, e.y);
        }

    }

    @Override
    public void setSelectionInRefactoring(SelectionEvent e) {
        AliExtorRefactoring refactoring = (AliExtorRefactoring) getRefactoring();
        refactoring.setJustRefactorSelected(rbtnJustSelected.getSelection());
        if (cbxCreateTemplateAlias != null) {
            refactoring.setUserWantsTemplateAlias(cbxCreateTemplateAlias.getSelection());
        }
    }

}
