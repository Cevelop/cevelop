package com.cevelop.clonewar.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;
import com.cevelop.clonewar.view.util.TypePair;


/**
 * Wizard page to select the types for extraction.
 *
 * @author ythrier(at)hsr.ch
 */

public abstract class ETTPSelectionWizardPage extends AbstractETTPWizardPage {

    private static final int    CONTROL_PAGE_LAYOUT_COL = 1;
    private static final int    MIDDLE_PAGE_LAYOUT_COL  = 2;
    private static final int    ORDERING_PREVIEW_INDENT = 5;
    private static final String COMMA_SPACE             = ", ";
    private static final String TYPENAME                = "typename ";
    private static final String TEMPLATE_TEXT           = "template";
    private static final String OPEN_BRACKET            = "<";
    private static final String CLOSE_BRACKET           = ">";
    private static final int    PAGE_LAYOUT_COL         = 1;
    private boolean             flipToNextPage          = true;
    private Button              addButton;
    private Button              removeButton;
    private Button              upButton;
    private Button              downButton;
    private Label               orderPreview;
    private Table               selectionTable;

    /**
     * Create the wizard page.
     */
    public ETTPSelectionWizardPage() {
        super(Messages.EXTRACTION_PAGE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createComponents() {
        setMessage(Messages.EXTRACTION_PAGE_MAIN_MSG);
        addInformationLabel();
        addMiddlePage();
        addOrderPreview();
        selectionTable.setSelection(0);
        setButtonStates(selectionTable);
    }

    /**
     * Change the data of two table items.
     *
     * @param selection
     * Selected element.
     * @param replacement
     * Replace element.
     */
    private void swap(TableItem selection, TableItem replacement) {
        TypePair temp = (TypePair) selection.getData();
        selection.setData(replacement.getData());
        replacement.setData(temp);
        setNewText(selection);
        setNewText(replacement);
        boolean checked = selection.getChecked();
        selection.setChecked(replacement.getChecked());
        replacement.setChecked(checked);
    }

    /**
     * Set the new text to display for a given table item.
     *
     * @param item
     * Table item.
     */
    protected abstract void setNewText(TableItem item);

    /**
     * Change the next page processing.
     *
     * @param on
     * True to set on, false for off.
     */
    protected void changeNextPageProcessing(boolean on) {
        flipToNextPage = on;
        setPageComplete(on);
    }

    /**
     * Add the middle panel (control buttons and table for type selection).
     */
    private void addMiddlePage() {
        Composite composite = new Composite(getPage(), SWT.NONE);
        composite.setLayout(createMiddlePageLayout());
        composite.setLayoutData(new GridData());
        addSelectionTable(composite);
        addControlButtons(composite);
    }

    /**
     * Get the editable column.
     *
     * @return The editable column.
     */
    protected abstract int getEditableColumn();

    /**
     * Create the layout for the middle (control buttons and table) panel.
     *
     * @return Layout.
     */
    private Layout createMiddlePageLayout() {
        GridLayout layout = new GridLayout();
        layout.numColumns = MIDDLE_PAGE_LAYOUT_COL;
        return layout;
    }

    /**
     * Check whether a column can be edited.
     *
     * @param column
     * Column index.
     * @return True if the column can be edited, otherwise false.
     */
    protected abstract boolean canEditColumn(int column);

    /**
     * Add preview for the ordering.
     */
    private void addOrderPreview() {
        Label orderingTitle = new Label(getPage(), SWT.NONE);
        orderingTitle.setFont(createOrderingTitleFont(orderingTitle));
        orderingTitle.setText(Messages.ORDERING_TITLE);
        orderingTitle.setLayoutData(createOrderingLabelLayoutData());
        orderPreview = new Label(getPage(), SWT.NONE);
        orderPreview.setLayoutData(createOrderingLabelLayoutData());
        updateOrderPreview(selectionTable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFlipToNextPage() {
        return flipToNextPage;
    }

    /**
     * Update the order preview.
     *
     * @param table
     * Table with item ordering.
     */
    protected void updateOrderPreview(Table table) {
        String types = "";
        for (TableItem item : table.getItems()) {
            TypeInformation type = getTypePair(item).getTypeInfo();
            TransformAction action = getTypePair(item).getAction();
            if (action.shouldPerform() && !types.contains(type.getTemplateName() + COMMA_SPACE)) types += (TYPENAME + type.getTemplateName() +
                                                                                                           COMMA_SPACE);
        }
        if (types.isEmpty()) return;
        types = types.substring(0, types.length() - 2);
        orderPreview.setText("\t" + TEMPLATE_TEXT + OPEN_BRACKET + types.trim() + CLOSE_BRACKET);
        orderPreview.setSize(orderPreview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Return the type pair of a table item.
     *
     * @param item
     * Item.
     * @return Type pair.
     */
    protected TypePair getTypePair(TableItem item) {
        return (TypePair) item.getData();
    }

    /**
     * Create the layout data for the ordering label.
     *
     * @return Layout data.
     */
    private GridData createOrderingLabelLayoutData() {
        GridData layoutData = new GridData();
        layoutData.verticalIndent = ORDERING_PREVIEW_INDENT;
        layoutData.horizontalIndent = ORDERING_PREVIEW_INDENT;
        layoutData.grabExcessHorizontalSpace = true;
        return layoutData;
    }

    /**
     * Create the font for the ordering title.
     *
     * @param orderingTitle
     * Original label.
     * @return Font.
     */
    private Font createOrderingTitleFont(Label orderingTitle) {
        return new Font(Display.getCurrent(), createOrderingTitleFontData(orderingTitle));
    }

    /**
     * Create the ordering title font data.
     *
     * @param label
     * Original label.
     * @return Font data.
     */
    private FontData createOrderingTitleFontData(Label label) {
        FontData fontData = label.getFont().getFontData()[0];
        fontData.setStyle(SWT.BOLD);
        return fontData;
    }

    /**
     * Add the control buttons (up/down/remove/add).
     *
     * @param parent
     * Parent.
     */
    private void addControlButtons(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(createControlButtonsLayout());
        addButton = createButton(composite, Messages.ADD_TEXT);
        removeButton = createButton(composite, Messages.REM_TEXT);
        upButton = createButton(composite, Messages.UP_TEXT);
        downButton = createButton(composite, Messages.DOWN_TEXT);
        addButtonListener();
    }

    /**
     * Register button listeners.
     */
    private void addButtonListener() {
        addButton.addSelectionListener(new AddEntrySelectionListener(selectionTable));
        removeButton.addSelectionListener(new RemoveEntrySelectionListener(selectionTable));
        upButton.addSelectionListener(new EntryUpSelectionListener(selectionTable));
        downButton.addSelectionListener(new EntryDownSelectionListener(selectionTable));
    }

    /**
     * Create the layout for the control buttons.
     *
     * @return Layout.
     */
    private Layout createControlButtonsLayout() {
        GridLayout layout = new GridLayout();
        layout.numColumns = CONTROL_PAGE_LAYOUT_COL;
        return layout;
    }

    /**
     * Create a button.
     *
     * @param parent
     * Parent.
     * @param text
     * Text.
     * @return Button.
     */
    private Button createButton(Composite parent, String text) {
        Button button = new Button(parent, SWT.NONE);
        button.setText(text);
        button.setLayoutData(createButtonLayoutData());
        return button;
    }

    /**
     * Returns layout data for a button, grabbing horizontal space.
     *
     * @return Layout data.
     */
    private Object createButtonLayoutData() {
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.horizontalAlignment = GridData.FILL;
        return layoutData;
    }

    /**
     * Add the table for type selection.
     *
     * @param parent
     * Parent.
     */
    private void addSelectionTable(Composite parent) {
        selectionTable = new Table(parent, getTableFlags());
        selectionTable.setLayoutData(createTableLayoutData());
        selectionTable.setLinesVisible(true);
        selectionTable.addMouseListener(createTableSelectionListener());
        addColumnHeadings(selectionTable);
        addTableValues(selectionTable);
    }

    /**
     * Create the table selection listener.
     *
     * @return Listener.
     */
    private CellSelectionListener createTableSelectionListener() {
        return new CellSelectionListener(createTableEditor(selectionTable), selectionTable);
    }

    /**
     * Create a table editor.
     *
     * @param table
     * Table.
     * @return Table editor.
     */
    private TableEditor createTableEditor(Table table) {
        TableEditor editor = new TableEditor(table);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        return editor;
    }

    /**
     * Create the layout data for the table.
     *
     * @return Layout data.
     */
    private Object createTableLayoutData() {
        GridData layoutData = new GridData();
        layoutData.grabExcessVerticalSpace = true;
        layoutData.minimumHeight = 300;
        return layoutData;
    }

    /**
     * Add the table values.
     *
     * @param table
     * Table.
     */
    private void addTableValues(Table table) {
        for (TypeInformation type : getConfig().getAllTypesOrdered()) {
            for (TransformAction action : getConfig().getActionsOf(type)) {
                if (action.shouldPerform()) createTableItem(table, type, action);
            }
        }
    }

    /**
     * Create a table item.
     *
     * @param table
     * Table item.
     * @param type
     * Type info.
     * @param action
     * Transform action.
     */
    protected abstract void createTableItem(Table table, TypeInformation type, TransformAction action);

    /**
     * Add column headings.
     *
     * @param table
     * Table.
     */
    private void addColumnHeadings(Table table) {
        table.setHeaderVisible(true);
        for (int i = 0; i < getHeadings().length; ++i) {
            TableColumn column = createDefaultColumn(table);
            column.setText(getHeadings()[i]);
            column.pack();
        }
    }

    /**
     * Get the table column head names.
     *
     * @return String array of column heads.
     */
    protected abstract String[] getHeadings();

    /**
     * Create a default table column.
     *
     * @param table
     * Parent table.
     * @return Table column.
     */
    private TableColumn createDefaultColumn(Table table) {
        return new TableColumn(table, SWT.NULL);
    }

    /**
     * Return the table flags.
     *
     * @return Table flags.
     */
    protected abstract int getTableFlags();

    /**
     * Add a label to display some information about the refactoring.
     */
    private void addInformationLabel() {
        new Label(getPage(), SWT.NONE).setText(Messages.EXTRACTION_PAGE_INFO_MSG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Composite createPageComposite(Composite parent) {
        Composite page = new Composite(parent, SWT.NONE);
        page.setLayout(createPageLayout());
        page.setLayoutData(createPageLayoutData());
        return page;
    }

    /**
     * Check if the table already contains all types.
     *
     * @param table
     * Table.
     * @return True if the table contains all types, otherwise false.
     */
    private boolean hasAllTypes(Table table) {
        return table.getItemCount() == getConfig().getAllActions().size();
    }

    /**
     * Check if the selected item is the last item.
     *
     * @param table
     * Table.
     * @return True if the last item is selected, otherwise false.
     */
    private boolean isTableUpperBound(Table table) {
        return table.getSelectionIndex() == (table.getItemCount() - 1);
    }

    /**
     * Check if the selected item is the first item.
     *
     * @param table
     * Table.
     * @return True if the first item is selected, otherwise false.
     */
    private boolean isTableLowerBound(Table table) {
        return table.getSelectionIndex() == 0;
    }

    /**
     * Check if the table has a selection.
     *
     * @param table
     * Table.
     * @return True if a selection is available, otherwise false.
     */
    private boolean hasSelection(Table table) {
        return table.getSelectionIndex() != -1;
    }

    /**
     * Set the button state (on/off).
     *
     * @param table
     * Table.
     */
    private void setButtonStates(Table table) {
        addButton.setEnabled(!hasAllTypes(table));
        removeButton.setEnabled(hasSelection(table));
        upButton.setEnabled(hasSelection(table) && !isTableLowerBound(table));
        downButton.setEnabled(hasSelection(table) && !isTableUpperBound(table));
    }

    /**
     * Change the ordering (complete reordering).
     *
     * @param table
     * Table.
     */
    private void changeOrdering(Table table) {
        TableItem[] items = table.getItems();
        for (TableItem item : items)
            ((TypePair) item.getData()).getTypeInfo().setOrderId(-1);
        for (int i = 0; i < items.length; ++i)
            if (((TypePair) items[i].getData()).getTypeInfo().getOrderId() == -1) ((TypePair) items[i].getData()).getTypeInfo().setOrderId(i);
        updateOrderPreview(table);
    }

    /**
     * Check if there are any defaulting problems.
     */
    protected abstract void checkDefaultingProblem();

    /**
     * Create the page layout data.
     *
     * @return Layout data.
     */
    protected Object createPageLayoutData() {
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        return layoutData;
    }

    /**
     * Create the page layout.
     *
     * @return Layout.
     */
    protected Layout createPageLayout() {
        GridLayout layout = new GridLayout();
        layout.numColumns = PAGE_LAYOUT_COL;
        return layout;
    }

    /**
     * Listener to modify the extraction table. Adding new entries if there are
     * any available to create a new extraction type.
     *
     * @author ythrier(at)hsr.ch
     */
    private class AddEntrySelectionListener extends SelectionAdapter {

        private Table table;

        /**
         * Create the listener.
         *
         * @param table
         * The table to modify.
         */
        public AddEntrySelectionListener(Table table) {
            this.table = table;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            ElementListSelectionDialog dialog = createDialog();
            if (dialog.open() != Window.OK) {
                setButtonStates(table);
                return;
            }
            TypePair pair = findTypePair(dialog.getResult()[0]);
            if (pair == null) return;
            pair.getAction().setPerform(true);
            createTableItem(table, pair.getTypeInfo(), pair.getAction());
            changeOrdering(table);
            setButtonStates(table);
        }

        /**
         * Find the transformation action for the selected entry in the
         * selection dialog.
         *
         * @param selection
         * Selected entry.
         * @return Action related to the selected entry.
         */
        private TypePair findTypePair(Object selection) {
            for (TypeInformation type : getConfig().getAllTypes()) {
                for (TransformAction action : getConfig().getActionsOf(type)) {
                    if (action.getVariableName().equals(selection)) return new TypePair(type, action);
                }
            }
            return null;
        }

        /**
         * Create a dialog to select a type.
         *
         * @return Dialog.
         */
        private ElementListSelectionDialog createDialog() {
            ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
            dialog.setTitle(Messages.TYPE_SELECTION_DIALOG_TITLE);
            dialog.setMessage(Messages.TYPE_SELECTION_DIALOG_TEXT);
            dialog.setElements(createProposals());
            return dialog;
        }

        /**
         * Create an array of proposals for the list selection dialog.
         *
         * @return Proposal array.
         */
        private Object[] createProposals() {
            List<String> types = new ArrayList<>();
            for (TransformAction action : getConfig().getAllActions()) {
                if (!action.shouldPerform()) types.add(action.getVariableName());
            }
            return types.toArray();
        }
    }

    /**
     * Selection listener for the event of selecting the template name or the
     * defaulting property in a cell in the extraction table.
     *
     * @author ythrier(at)hsr.ch
     */
    private class CellSelectionListener extends MouseAdapter {

        private TableEditor editor;
        private Table       table;

        /**
         * Create the listener for the table.
         *
         * @param editor
         * Table editor.
         * @param table
         * Table.
         */
        public CellSelectionListener(TableEditor editor, Table table) {
            this.editor = editor;
            this.table = table;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseDown(MouseEvent e) {
            setButtonStates(table);
            changeNextPageProcessing(false);
            disposeOldEditor();
            TableItem item = findEventItem(createPoint(e));
            if (item == null) {
                changeNextPageProcessing(true);
                return;
            }
            getTypePair(item).getTypeInfo().setDefaulting(item.getChecked());
            checkDefaultingProblem();
            int col = findEventCol(item, createPoint(e));
            if (!canEditColumn(col)) {
                changeNextPageProcessing(true);
                return;
            }
            Text text = new Text(table, SWT.NONE);
            text.setText(item.getText(getEditableColumn()));
            addAutoCompletionTo(text);
            addListeners(text, item, col);
            text.selectAll();
            text.setFocus();
            editor.setEditor(text, item, getEditableColumn());
        }

        /**
         * Add the listeners configuration saving.
         *
         * @param text
         * Textfield.
         * @param col
         * Column.
         * @param item
         * Table item.
         */
        private void addListeners(Text text, TableItem item, int col) {
            Listener listener = createListener(item, col, text);
            text.addListener(SWT.FocusOut, listener);
            text.addListener(SWT.Traverse, listener);
        }

        /**
         * Create a listener for the focus out and traverse events.
         *
         * @param item
         * Table item.
         * @param col
         * Column.
         * @param text
         * Textfield.
         * @return Listener.
         */
        private Listener createListener(TableItem item, int col, Text text) {
            return new SaveConfigurationListener(item, col, text);
        }

        /**
         * Add auto completion ability to a text field.
         *
         * @param text
         * Textfield.
         */
        private void addAutoCompletionTo(Text text) {
            new AutoCompleteField(text, new TextContentAdapter(), lookupProposals());
        }

        /**
         * Lookup possible values for the auto completion.
         *
         * @return Array of proposals.
         */
        private String[] lookupProposals() {
            List<String> proposals = new ArrayList<>(getConfig().getAllTypes().size());
            for (TypeInformation type : getConfig().getAllTypesOrdered()) {
                proposals.add(type.getTemplateName());
            }
            return proposals.toArray(new String[proposals.size()]);
        }

        /**
         * Search for the column in which the event occurred.
         *
         * @param item
         * Table item.
         * @param point
         * Event location.
         * @return The event column or -1, if no column was found.
         */
        private int findEventCol(TableItem item, Point point) {
            for (int i = 0; i < table.getColumnCount(); ++i)
                if (item.getBounds(i).contains(point)) return i;
            return -1;
        }

        /**
         * Search for the table item on which the event occurred.
         *
         * @param point
         * Event location.
         * @return The item or null, if no item was found.
         */
        private TableItem findEventItem(Point point) {
            return table.getItem(point);
        }

        /**
         * Create a point from the event.
         *
         * @param e
         * Event.
         * @return Point.
         */
        private Point createPoint(MouseEvent e) {
            return new Point(e.x, e.y);
        }

        /**
         * Dispose the old editor control if necessary.
         */
        private void disposeOldEditor() {
            if (editor.getEditor() != null) editor.getEditor().dispose();
        }
    }

    /**
     * This listener saves the value entered in the textfield when editing a
     * template name in the table in the configuration.
     *
     * @author ythrier(at)hsr.ch
     */
    private class SaveConfigurationListener implements Listener {

        private static final String FIRST_REPLACE  = "?1";
        private static final String SECOND_REPLACE = "?2";
        private TableItem           item;
        private int                 column;
        private Text                text;

        /**
         * Create the listener to save the configuration.
         *
         * @param item
         * Table item.
         * @param column
         * Editing column.
         * @param text
         * Textfield with edited configuration value.
         */
        public SaveConfigurationListener(TableItem item, int column, Text text) {
            this.item = item;
            this.column = column;
            this.text = text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void handleEvent(Event event) {
            if (handleFocusOut(event)) {
                disposeAndFocus();
                return;
            }
            if (handleTraverseEvent(event)) {
                disposeAndFocus();
                return;
            }
        }

        /**
         * Dispose components and set new focus.
         */
        private void disposeAndFocus() {
            text.dispose();
            item.getParent().setFocus();
        }

        /**
         * Handle traverse event type.
         *
         * @param event
         * Event.
         * @return True if the event was handled.
         */
        private boolean handleTraverseEvent(Event event) {
            if (event.type == SWT.Traverse) {
                if (handleTraverseReturn(event)) return true;
                if (handleTraverseEscape(event)) return true;
            }
            return false;
        }

        /**
         * Handle traverse return event.
         *
         * @param event
         * Event.
         * @return True if the event is a return event.
         */
        private boolean handleTraverseReturn(Event event) {
            if (event.detail == SWT.TRAVERSE_RETURN) {
                save();
                changeNextPageProcessing(true);
                return true;
            }
            return false;
        }

        /**
         * Handle traverse escape event type.
         *
         * @param event
         * Event.
         * @return True if the event is a traverse escatext_.traverse(traversal,
         * event)pe event, otherwise false.
         */
        private boolean handleTraverseEscape(Event event) {
            if (event.detail == SWT.TRAVERSE_ESCAPE) {
                event.doit = false;
                changeNextPageProcessing(true);
                return true;
            }
            return false;
        }

        /**
         * Handle focus out event type.
         *
         * @param event
         * Event.
         * @return True if the event was handled.
         */
        private boolean handleFocusOut(Event event) {
            if (event.type == SWT.FocusOut) {
                save();
                changeNextPageProcessing(true);
                return true;
            }
            return false;
        }

        /**
         * Save the value entered.
         */
        private void save() {
            item.setText(column, text.getText());
            adjustConfiguration();
            updateOrderPreview(item.getParent());
        }

        /**
         * Adjust the configuration with the new template name value.
         */
        private void adjustConfiguration() {
            TypeInformation typeInfo = getTypePair(item).getTypeInfo();
            TransformAction action = getTypePair(item).getAction();
            List<TransformAction> actionList = getConfig().getActionsOf(typeInfo);
            actionList.remove(action);
            if (actionList.isEmpty()) getConfig().remove(typeInfo);
            TypeInformation newTypeInfo = copy(typeInfo);
            newTypeInfo.setTemplateName(item.getText(getEditableColumn()));
            if (!getConfig().getAllTypes().contains(newTypeInfo)) getConfig().add(newTypeInfo, new ArrayList<TransformAction>());
            getConfig().getActionsOf(newTypeInfo).add(action);
            item.setData(new TypePair(newTypeInfo, action));
            checkConversionProblems(newTypeInfo.getType(), newTypeInfo.getTemplateName(), action.getVariableName());
        }

        /**
         * Create a copy of the type info.
         *
         * @param typeInfo
         * Type info to copy.
         * @return Copy.
         */
        private TypeInformation copy(TypeInformation typeInfo) {
            TypeInformation newTypeInfo = new TypeInformation(typeInfo.getType());
            newTypeInfo.setDefaultType(typeInfo.getDefaultType());
            return newTypeInfo;
        }

        /**
         * Check if there may are conversion problems when using the same
         * template type for different original types.
         *
         * @param type
         * Type.
         * @param templateName
         * Template name.
         * @param varName
         * Variable name.
         */
        private void checkConversionProblems(IType type, String templateName, String varName) {
            for (TypeInformation typeInfo : getConfig().getAllTypes()) {
                if (typeInfo.getTemplateName().equals(templateName)) {
                    if (!(CPPVisitor.isSameType(typeInfo.getType(), type))) {
                        setMessage(createConversionWarning(templateName, varName), IMessageProvider.WARNING);
                        return;
                    }
                }
            }
            setMessage(Messages.EXTRACTION_PAGE_MAIN_MSG);
        }

        /**
         * Create a warning message.
         *
         * @param templateName
         * Template name.
         * @param varName
         * Variable name.
         * @return Warning text.
         */
        private String createConversionWarning(String templateName, String varName) {
            return Messages.TYPE_CONVERSION_WARNING.replace(FIRST_REPLACE, templateName).replace(SECOND_REPLACE, varName);
        }
    }

    /**
     * Listener to modify the extraction table. Removing a selected entry.
     *
     * @author ythrier(at)hsr.ch
     */
    private class RemoveEntrySelectionListener extends SelectionAdapter {

        private Table table;

        /**
         * Create the listener.
         *
         * @param table
         * Table to modify.
         */
        public RemoveEntrySelectionListener(Table table) {
            this.table = table;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (table.getSelectionIndex() == -1) return;
            TableItem item = table.getItem(table.getSelectionIndex());
            if (item == null) return;
            TypePair pair = (TypePair) item.getData();
            pair.getAction().setPerform(false);
            table.remove(table.getSelectionIndex());
            changeOrdering(table);
            setButtonStates(table);
            updateOrderPreview(table);
        }
    }

    private abstract class EntrySelectionListener extends SelectionAdapter {

        protected Table table;

        protected EntrySelectionListener(Table table) {
            this.table = table;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            int selectionIndex = table.getSelectionIndex();
            if (checkSelection(selectionIndex)) {
                return;
            }
            TableItem selection = table.getItem(selectionIndex);
            TableItem replacement = table.getItem(newIndex(selectionIndex));
            swap(selection, replacement);
            changeOrdering(table);
            table.setSelection(newIndex(selectionIndex));
            setButtonStates(table);
            checkDefaultingProblem();
        }

        protected abstract boolean checkSelection(int selectionIndex);

        protected abstract int newIndex(int selectionIndex);

    }

    /**
     * Listener to modify the extraction table. Moving the selected entry up.
     *
     * @author ythrier(at)hsr.ch
     */
    private class EntryUpSelectionListener extends EntrySelectionListener {

        /**
         * Create the listener.
         *
         * @param table
         * Table to modify.
         */
        public EntryUpSelectionListener(Table table) {
            super(table);
        }

        @Override
        protected boolean checkSelection(int selectionIndex) {
            return selectionIndex == -1 || selectionIndex == 0;
        }

        @Override
        protected int newIndex(int selectionIndex) {
            return selectionIndex - 1;
        }
    }

    /**
     * Listener to modify the extraction table. Moving the selected entry down.
     *
     * @author ythrier(at)hsr.ch
     *
     */
    private class EntryDownSelectionListener extends EntrySelectionListener {

        /**
         * Create the listener.
         *
         * @param table
         * Table to modify.
         */
        public EntryDownSelectionListener(Table table) {
            super(table);
        }

        @Override
        protected boolean checkSelection(int selectionIndex) {
            return selectionIndex == -1 || selectionIndex == table.getItemCount();
        }

        @Override
        protected int newIndex(int selectionIndex) {
            return selectionIndex + 1;
        }
    }
}
