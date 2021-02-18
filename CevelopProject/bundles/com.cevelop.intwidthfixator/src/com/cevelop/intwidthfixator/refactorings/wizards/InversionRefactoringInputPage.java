package com.cevelop.intwidthfixator.refactorings.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.cevelop.intwidthfixator.helpers.AbstractHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper;
import com.cevelop.intwidthfixator.helpers.IdHelper.WidthId;
import com.cevelop.intwidthfixator.refactorings.inversion.InversionRefactoringInfo;

import ch.hsr.ifs.iltis.cpp.core.ui.refactoring.SelectionRefactoring;


/**
 * @author tstauber
 */
public class InversionRefactoringInputPage extends AbstractRefactoringInputPage<InversionRefactoringInfo> {

    public static final String  LABEL_TARGET_TYPE = "std::%s_t -> %s";            //$NON-NLS-1$
    private static final String U_ICON_PATH       = "resources/icons/fixated.png";

    private Button unsignedCheckbox;
    private Button signedCheckbox;

    private Button int8Checkbox;
    private Button int16Checkbox;
    private Button int32Checkbox;
    private Button int64Checkbox;

    public InversionRefactoringInputPage() {
        this(Messages.InversionRefactoringInputPage_2);
    }

    public InversionRefactoringInputPage(final String pageName) {
        super(pageName, Messages.InversionRefactoringInputPage_3, IdHelper.DS_INVERSION_REFACTORING_PREFIX);
        setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IdHelper.PLUGIN_ID, U_ICON_PATH));
    }

    protected void initSettings() {

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
        questionLabel.setText(Messages.InversionRefactoringInputPage_0);

        unsignedCheckbox = createCheckBox(Messages.InversionRefactoringInputPage_5, ".unsigned"); // $NON-NLS-2$
        signedCheckbox = createCheckBox(Messages.InversionRefactoringInputPage_7, ".signed"); // $NON-NLS-2$

        int8Checkbox = createTypeCheckBox(WidthId.WIDTH_8);
        int16Checkbox = createTypeCheckBox(WidthId.WIDTH_16);
        int32Checkbox = createTypeCheckBox(WidthId.WIDTH_32);
        int64Checkbox = createTypeCheckBox(WidthId.WIDTH_64);

        setPageComplete(isAtLeastOneSelected());

        updateInfo();
        Dialog.applyDialogFont(canvas);
    }

    @Override
    protected boolean isAtLeastOneSelected() {
        return isSignedOrUnsignedChecked() && isAtLeastOneTypeChecked();
    }

    private boolean isSignedOrUnsignedChecked() {
        return signedCheckbox.getSelection() || unsignedCheckbox.getSelection();
    }

    private boolean isAtLeastOneTypeChecked() {
        return int8Checkbox.getSelection() || int16Checkbox.getSelection() || int32Checkbox.getSelection() || int64Checkbox.getSelection();
    }

    private Button createTypeCheckBox(final WidthId targetWidth) {
        return createCheckBox(String.format(LABEL_TARGET_TYPE, targetWidth.width, getTypeString(targetWidth)), ".int" + targetWidth.width); //$NON-NLS-1$
    }

    private String getTypeString(final WidthId typeWidth) {
        if (getWidthIdFromPreferenceId(IdHelper.P_CHAR_MAPPING) == typeWidth) {
            return Messages.InversionRefactoringInputPage_10;
        }
        if (getWidthIdFromPreferenceId(IdHelper.P_SHORT_MAPPING) == typeWidth) {
            return Messages.InversionRefactoringInputPage_11;
        }
        if (getWidthIdFromPreferenceId(IdHelper.P_INT_MAPPING) == typeWidth) {
            return Messages.InversionRefactoringInputPage_12;
        }
        if (getWidthIdFromPreferenceId(IdHelper.P_LONG_MAPPING) == typeWidth) {
            return Messages.InversionRefactoringInputPage_13;
        }
        if (getWidthIdFromPreferenceId(IdHelper.P_LONGLONG_MAPPING) == typeWidth) {
            return Messages.InversionRefactoringInputPage_14;
        }
        return null;
    }

    private WidthId getWidthIdFromPreferenceId(final String id) {
        return AbstractHelper.getWidthIdFromPreferenceId(id, getRefactoring().getProject().getProject());
    }

    @Override
    protected void updateInfo() {
        final SelectionRefactoring<InversionRefactoringInfo> refactoring = getRefactoring();
        if (refactoring != null) {
            final InversionRefactoringInfo info = refactoring.getRefactoringInfo();
            info.refactor_int8 = int8Checkbox.getSelection();
            info.refactor_int16 = int16Checkbox.getSelection();
            info.refactor_int32 = int32Checkbox.getSelection();
            info.refactor_int64 = int64Checkbox.getSelection();

            info.refactor_signed = signedCheckbox.getSelection();
            info.refactor_unsigned = unsignedCheckbox.getSelection();
        }
    }

}
