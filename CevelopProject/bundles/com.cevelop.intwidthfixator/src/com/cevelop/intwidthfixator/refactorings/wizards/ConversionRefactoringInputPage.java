package com.cevelop.intwidthfixator.refactorings.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.refactorings.conversion.ConversionInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;


/**
 * @author tstauber
 */
public class ConversionRefactoringInputPage extends AbstractRefactoringInputPage<ConversionInfo> {

    public static final String  LABEL_TARGET_TYPE = "%s -> std::int%s_t";           //$NON-NLS-1$
    private static final String U_ICON_PATH       = "resources/icons/unfixated.png";

    private Button charCheckbox;
    private Button shortCheckbox;
    private Button intCheckbox;
    private Button longCheckbox;
    private Button longlongCheckbox;

    public ConversionRefactoringInputPage() {
        this(Messages.ConversionRefactoringInputPage_1);
    }

    public ConversionRefactoringInputPage(final String pageName) {
        super(pageName, Messages.ConversionRefactoringInputPage_2, IdHelper.DS_CONVERSION_REFACTORING_PREFIX);
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.PLUGIN_ID, U_ICON_PATH));
    }

    @Override
    public void createControl(final Composite parent) {
        setPageComplete(false);

        canvas = new Composite(parent, SWT.NONE);
        setControl(canvas);
        initializeDialogUnits(canvas);

        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 10;
        canvas.setLayout(layout);

        final GridData gd = new GridData();
        gd.horizontalSpan = 2;

        final Label questionLabel = new Label(canvas, SWT.NONE);
        questionLabel.setLayoutData(gd);
        questionLabel.setText(Messages.ConversionRefactoringInputPage_4);

        charCheckbox = createCheckBox("char", IdHelper.P_CHAR_MAPPING); //$NON-NLS-1$
        shortCheckbox = createCheckBox("short int", IdHelper.P_SHORT_MAPPING); //$NON-NLS-1$
        intCheckbox = createCheckBox("int", IdHelper.P_INT_MAPPING); //$NON-NLS-1$
        longCheckbox = createCheckBox("long int", IdHelper.P_LONG_MAPPING); //$NON-NLS-1$
        longlongCheckbox = createCheckBox("long long int", IdHelper.P_LONGLONG_MAPPING); //$NON-NLS-1$

        setPageComplete(isAtLeastOneSelected());

        updateInfo();
        Dialog.applyDialogFont(canvas);
    }

    @Override
    protected Button createCheckBox(final String labelText, final String settingName) {
        return super.createCheckBox(String.format(LABEL_TARGET_TYPE, labelText, getWidthForPref(settingName)), settingName);
    }

    @Override
    protected boolean isAtLeastOneSelected() {
        return charCheckbox.getSelection() || shortCheckbox.getSelection() || intCheckbox.getSelection() || longCheckbox.getSelection() ||
               longlongCheckbox.getSelection();
    }

    @Override
    protected void updateInfo() {
        final SelectionRefactoring<ConversionInfo> refactoring = getRefactoring();
        if (refactoring != null) {
            final ConversionInfo crInfo = refactoring.getRefactoringInfo();
            crInfo.refactor_char = charCheckbox.getSelection();
            crInfo.refactor_short = shortCheckbox.getSelection();
            crInfo.refactor_int = intCheckbox.getSelection();
            crInfo.refactor_long = longCheckbox.getSelection();
            crInfo.refactor_longlong = longlongCheckbox.getSelection();
        }
    }
}
