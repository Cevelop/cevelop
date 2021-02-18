package com.cevelop.clonewar.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;
import com.cevelop.clonewar.view.util.TypePair;


/**
 * Wizard page for type selection of the type transformation.
 *
 * @author ythrier(at)hsr.ch
 */
public class ETTPTypeSelectionWizardPage extends ETTPSelectionWizardPage {

    private static final int EDITABLE_COLUMN = 3;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setNewText(TableItem item) {
        TypePair pair = getTypePair(item);
        int i = 1;
        item.setText(i++, pair.getAction().getVariableName());
        item.setText(i++, pair.getTypeInfo().getTypeName());
        item.setText(i++, pair.getTypeInfo().getTemplateName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getEditableColumn() {
        return EDITABLE_COLUMN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canEditColumn(int column) {
        return !(column == -1 || column != EDITABLE_COLUMN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTableItem(Table table, TypeInformation type, TransformAction action) {
        TableItem item = new TableItem(table, SWT.NONE);
        int i = 1;
        item.setChecked(type.shouldDefault());
        item.setText(i++, action.getVariableName());
        item.setText(i++, type.getTypeName());
        item.setText(i++, type.getTemplateName());
        item.setData(new TypePair(type, action));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String[] getHeadings() {
        return Messages.TYPE_SELECTION_TABLE_HEADINGS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getTableFlags() {
        return SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkDefaultingProblem() {
        boolean defaulting = false;
        for (TypeInformation type : getConfig().getAllTypesOrdered()) {
            if (!getConfig().hasPerformableAction(type)) continue;
            if (type.shouldDefault() && !defaulting) {
                defaulting = true;
                continue;
            }
            if (!type.shouldDefault() && defaulting) {
                setErrorMessage(Messages.INVALID_DEFAULTING);
                changeNextPageProcessing(false);
                return;
            }
        }
        setErrorMessage(null);
        setMessage(Messages.EXTRACTION_PAGE_MAIN_MSG);
        changeNextPageProcessing(true);
    }
}
