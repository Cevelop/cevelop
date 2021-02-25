/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import com.cevelop.includator.ui.helpers.ColumnNotifyingToolTipSupport;
import com.cevelop.includator.ui.solutionoperations.SuggestionSolutionOperation;


public class SuggestionTree extends TreeViewer {

    private final SuggestionContentProvider contentProvider;
    private final SuggestionLabelProvider   labelProvider;

    public SuggestionTree(Composite parent, SuggestionContentProvider contentProvider, SuggestionLabelProvider labelProvider) {
        super(parent, SWT.FULL_SELECTION);
        new ColumnNotifyingToolTipSupport(this, labelProvider);
        getTree().setHeaderVisible(true);
        getTree().setLinesVisible(true);
        this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
        setContentProvider(contentProvider);
        createColumns();
        setLabelProvider(labelProvider);
        setInput("");
    }

    @Override
    public ViewerRow getViewerRow(Point point) {
        return super.getViewerRow(point);
    }

    public void shrinkToSeeAllColumns(int hintWith) {
        Iterator<TreeColumn> iter = Arrays.asList(getTree().getColumns()).iterator();
        TreeColumn firstColumn = iter.next();
        int otherColsWith = 0;
        while (iter.hasNext()) {
            TreeColumn col = iter.next();
            otherColsWith += col.getWidth();
        }
        firstColumn.setWidth(Math.max(100, hintWith - otherColsWith));
    }

    public void packColumns() {
        expandToLevel(3);
        for (TreeColumn curColumn : getTree().getColumns()) {
            curColumn.pack();
        }
    }

    private void createColumns() {
        List<SuggestionSolutionOperation> allOperations = collectItemOperations();
        labelProvider.setColumnOperationList(allOperations);
        createColumn("Suggestion", "Suggestion");
        for (SuggestionSolutionOperation curOperation : allOperations) {
            TreeViewerColumn column = createColumn(curOperation.getColumnDispalyName(), curOperation.getColumnToolTipText());
            addEditingSupport(column, curOperation);
        }

    }

    private List<SuggestionSolutionOperation> collectItemOperations() {
        return new ArrayList<>(contentProvider.getTopElement().getPossibleOperations());
    }

    private TreeViewerColumn createColumn(String text, String toolTipText) {
        TreeViewerColumn column = new TreeViewerColumn(this, SWT.NONE);
        column.getColumn().setText(text);
        column.getColumn().setToolTipText(toolTipText);
        return column;
    }

    private void addEditingSupport(TreeViewerColumn column, final SuggestionSolutionOperation operation) {
        column.setEditingSupport(new EditingSupport(this) {

            private final CellEditor editor = new CheckboxCellEditor(getTree());

            private SuggestionTreeElement getElement(Object element) {
                return (SuggestionTreeElement) element;
            }

            @Override
            protected void setValue(Object element, Object value) {
                getElement(element).setChecked(operation, true);
                SuggestionTreeElement topElement = contentProvider.getTopElement();
                topElement.inheritValueFromChildren();
                updateElement(getElement(element));
            }

            @Override
            protected Object getValue(Object element) {
                return getElement(element).isChecked(operation);
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return editor;
            }

            @Override
            protected boolean canEdit(Object element) {
                return getElement(element).hasOperation(operation);
            }
        });
    }

    protected void updateElement(SuggestionTreeElement changingElement) {
        ArrayList<SuggestionTreeElement> all = new ArrayList<>();
        addParents(changingElement, all);
        addAllChildren(changingElement, all);
        update(all.toArray(), null);
    }

    private void addAllChildren(SuggestionTreeElement element, ArrayList<SuggestionTreeElement> all) {
        all.add(element);
        for (Object curChild : element.getChildren()) {
            addAllChildren((SuggestionTreeElement) curChild, all);
        }
    }

    private void addParents(SuggestionTreeElement changingElement, ArrayList<SuggestionTreeElement> all) {
        while (changingElement.getParent() != null) {
            changingElement = changingElement.getParent();
            all.add(changingElement);
        }
    }
}
