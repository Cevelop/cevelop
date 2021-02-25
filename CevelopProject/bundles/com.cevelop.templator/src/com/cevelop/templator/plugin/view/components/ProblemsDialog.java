package com.cevelop.templator.plugin.view.components;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.templator.plugin.asttools.data.SubNameErrorCollection;
import com.cevelop.templator.plugin.asttools.data.SubNameErrorCollection.SubNameError;
import com.cevelop.templator.plugin.util.EclipseUtil;


public class ProblemsDialog extends Dialog {

    private SubNameErrorCollection subNameErrors;

    private List       errorList;
    private Label      errorImageLabel;
    private Label      titleLabel;
    private Label      messageLabel;
    private StyledText stackTraceText;

    private Group detailGroup;

    public ProblemsDialog(Shell parent, SubNameErrorCollection subNameErrors) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE);

        this.subNameErrors = subNameErrors;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, true));

        GridData gridData = new GridData(GridData.FILL_BOTH);
        SashForm sashForm = new SashForm(container, SWT.VERTICAL);
        sashForm.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        errorList = new List(sashForm, SWT.BORDER | SWT.V_SCROLL);
        errorList.setLayoutData(gridData);
        errorList.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.stateMask == SWT.CONTROL) {
                    navigateToErrorName(errorList.getSelectionIndex());
                }
                errorListSelectionChanged(errorList.getSelectionIndex());
            }
        });

        gridData = new GridData(GridData.FILL_BOTH);
        detailGroup = new Group(sashForm, SWT.NONE);
        detailGroup.setLayoutData(gridData);
        detailGroup.setText("Problem Details");
        detailGroup.setLayout(new GridLayout(2, false));

        gridData = new GridData();
        gridData.verticalSpan = 2;
        gridData.horizontalIndent = 8;
        Image errorImage = parent.getDisplay().getSystemImage(SWT.ICON_ERROR);
        errorImageLabel = new Label(detailGroup, SWT.NONE);
        errorImageLabel.setImage(errorImage);
        errorImageLabel.setLayoutData(gridData);
        errorImageLabel.setVisible(false);
        errorImageLabel.setSize(40, 40);

        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = false;
        gridData.horizontalIndent = 8;
        titleLabel = new Label(detailGroup, SWT.WRAP);
        titleLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = false;
        gridData.verticalIndent = 12;
        gridData.horizontalIndent = 8;
        messageLabel = new Label(detailGroup, SWT.WRAP);
        messageLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        stackTraceText = new StyledText(detailGroup, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        stackTraceText.setLayoutData(gridData);
        stackTraceText.setEditable(false);

        sashForm.setWeights(new int[] { 1, 2 });

        updateNameErrors();

        return container;
    }

    private void navigateToErrorName(int selectionIndex) {
        SubNameError selectedError = getErrorFromIndex(selectionIndex);
        EclipseUtil.navigateToNode(selectedError.getName().getOriginalNode());
    }

    private void errorListSelectionChanged(int selectionIndex) {

        SubNameError selectedError = getErrorFromIndex(selectionIndex);

        if (selectionIndex < subNameErrors.getDeductionErrors().size()) {
            titleLabel.setText("Deduction Error: " + selectedError.getName().toString());
        } else {
            titleLabel.setText("Resolving Error: " + selectedError.getName().toString());
        }

        errorImageLabel.setVisible(true);
        messageLabel.setText(selectedError.getException().getMessage());
        stackTraceText.setText(printStackTraceString(selectedError.getException()));
        detailGroup.layout();
    }

    private String printStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    private SubNameError getErrorFromIndex(int errorIndex) {
        if (errorIndex < subNameErrors.getDeductionErrors().size()) {
            return subNameErrors.getDeductionErrors().get(errorIndex);
        } else {
            errorIndex -= subNameErrors.getDeductionErrors().size();
            return subNameErrors.getResolvingErrors().get(errorIndex);
        }
    }

    public void updateNameErrors() {
        for (SubNameError error : subNameErrors.getDeductionErrors()) {
            errorList.add(error.getName().toString());
        }
        for (SubNameError error : subNameErrors.getResolvingErrors()) {
            errorList.add(error.getName().toString());
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, 600);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Problems");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Close", true);
    }

}
