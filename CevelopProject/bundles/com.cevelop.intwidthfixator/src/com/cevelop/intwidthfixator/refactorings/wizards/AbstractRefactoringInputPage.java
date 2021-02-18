package com.cevelop.intwidthfixator.refactorings.wizards;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.intwidthfixator.helpers.AbstractHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;


/**
 * @author tstauber
 */
public abstract class AbstractRefactoringInputPage<T extends MarkerInfo<T>> extends UserInputWizardPage {

    protected final String DS_SETTING_PREFIX;

    protected Composite canvas;

    public AbstractRefactoringInputPage(final String pageName, final String description, final String settingPrefix) {
        super(pageName);
        DS_SETTING_PREFIX = settingPrefix;
        setDescription(description);
        setTitle(getName());
        setPageComplete(false);
    }

    protected Button createCheckBox(final String labelText, final String settingName) {
        final Button checkBox = new Button(canvas, SWT.CHECK);
        checkBox.setText(labelText);
        checkBox.setSelection(getDialogSettings().getBoolean(DS_SETTING_PREFIX + settingName));
        checkBox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                setPageComplete(isAtLeastOneSelected());
                getDialogSettings().put(DS_SETTING_PREFIX + settingName, ((Button) e.widget).getSelection());
                updateInfo();
            }
        });
        return checkBox;
    }

    /**
     * Used to enable / disable finish button.
     * 
     * @return {@code true} iff at least one is selected
     */
    protected abstract boolean isAtLeastOneSelected();

    /**
     * Updates the fields of the {@link MarkerInfo}. Call after changes to
     * the selection were made.
     */
    protected abstract void updateInfo();

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean performFinish() {
        updateInfo();
        return super.performFinish();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SelectionRefactoring<T> getRefactoring() {
        return (SelectionRefactoring<T>) super.getRefactoring();
    }

    /**
     * Helper method to get the width from the {@link WidthId} stored in the
     * value for {@code key}.
     * 
     * @param key
     * The key from which to get the width
     * 
     * @return The width
     */
    protected int getWidthForPref(final String key) {
        return AbstractHelper.getWidthIdFromPreferenceId(key, getRefactoring().getProject().getProject()).width;
    }
}
