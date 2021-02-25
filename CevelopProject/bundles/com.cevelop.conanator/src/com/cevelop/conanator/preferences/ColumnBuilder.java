package com.cevelop.conanator.preferences;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;


public class ColumnBuilder {

    public static <T> void createColumns(TableViewer viewer, List<ColumnData<T>> columns) {
        TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));
        ColumnViewerEditorActivationStrategy actSupport = createActivationSupport(viewer);
        int feature = createFeature();

        TableViewerEditor.create(viewer, focusCellManager, actSupport, feature);

        final TextCellEditor editor = new TextCellEditor(viewer.getTable());

        for (ColumnData<T> columnData : columns) {
            TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
            createColumn(viewer, viewerColumn, columnData, editor);
            formatColumn(viewerColumn, columnData);
        }
    }

    public static <T> void createColumns(TreeViewer viewer, List<ColumnData<T>> columns) {
        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));
        ColumnViewerEditorActivationStrategy actSupport = createActivationSupport(viewer);
        int feature = createFeature();

        TreeViewerEditor.create(viewer, focusCellManager, actSupport, feature);

        final TextCellEditor editor = new TextCellEditor(viewer.getTree());

        for (ColumnData<T> columnData : columns) {
            TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
            createColumn(viewer, viewerColumn, columnData, editor);
            formatColumn(viewerColumn, columnData);
        }
    }

    private static int createFeature() {
        return ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL |
               ColumnViewerEditor.KEYBOARD_ACTIVATION;
    }

    private static ColumnViewerEditorActivationStrategy createActivationSupport(ColumnViewer viewer) {
        return new ColumnViewerEditorActivationStrategy(viewer) {

            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL ||
                       event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION ||
                       (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) ||
                       event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> void createColumn(ColumnViewer viewer, ViewerColumn viewerColumn, ColumnData<T> columnData, TextCellEditor editor) {

        ColumnLabelProvider provider = columnData.labelProvider;
        viewerColumn.setLabelProvider(provider != null ? provider : new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return columnData.getter.apply(((T) element));
            }
        });

        viewerColumn.setEditingSupport(new EditingSupport(viewer) {

            @Override
            protected void setValue(Object element, Object value) {
                columnData.setter.accept((T) element, (String) value);
                viewer.update(element, null);
            }

            @Override
            protected Object getValue(Object element) {
                return columnData.getter.apply(((T) element));
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return editor;
            }

            @Override
            protected boolean canEdit(Object element) {
                return columnData.canEdit.test((T) element);
            }
        });
    }

    private static <T> void formatColumn(TableViewerColumn viewerColumn, ColumnData<T> columnData) {
        TableColumn column = viewerColumn.getColumn();
        column.setText(columnData.title);
        column.setWidth(columnData.width);
        column.setResizable(true);
    }

    private static <T> void formatColumn(TreeViewerColumn viewerColumn, ColumnData<T> columnData) {
        TreeColumn column = viewerColumn.getColumn();
        column.setText(columnData.title);
        column.setWidth(columnData.width);
        column.setResizable(true);
    }

}
