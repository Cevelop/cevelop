package com.cevelop.aliextor.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public class FunctionRefactoringWizardPage extends SimpleRefactoringWizardPage {

    private static String TEXT_RADIO_DECLARATION = "Extract function declaration";
    private static String TEXT_RADIO_POINTER     = "Extract function pointer";
    private static String TEXT_RADIO_REFERENCE   = "Extract function reference";

    private Button cbxExtractFunctionDeclaration;
    private Button cbxExtractFuctionPointer;
    private Button cbxExtractFuctionReference;

    public FunctionRefactoringWizardPage(String name) {
        super(name);
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        rbtnRefactorAll.setText(rbtnRefactorAll.getText() + " occurrences of selected function");

        // Empty Line
        createDummyLabel();
        createDummyLabel();

        cbxExtractFunctionDeclaration = createRadioButton(cbxExtractFunctionDeclaration, TEXT_RADIO_DECLARATION, SWT.CHECK);
        cbxExtractFunctionDeclaration.setSelection(true);

        // Somehow the focus has to be set after the setSelection
        theUserInput.setFocus();

        cbxExtractFuctionPointer = createRadioButton(cbxExtractFuctionPointer, TEXT_RADIO_POINTER, SWT.CHECK);

        cbxExtractFuctionReference = createRadioButton(cbxExtractFuctionReference, TEXT_RADIO_REFERENCE, SWT.CHECK);

        setSelectionInRefactoring(null);

        // required to avoid an error in the system
        setControl(container);
        setPageComplete(false);
    }

    @Override
    public void setSelectionInRefactoring(SelectionEvent e) {
        if (cbxExtractFunctionDeclaration == null) {
            return;
        }
        checkSelection(e);
        AliExtorRefactoring refactoring = ((AliExtorRefactoring) getRefactoring());
        refactoring.setExtractFunctionDeclaration(cbxExtractFunctionDeclaration.getSelection());
        refactoring.setExtractFunctionPointer(cbxExtractFuctionPointer.getSelection());
        refactoring.setExtractFunctionReference(cbxExtractFuctionReference.getSelection());
        setPageComplete();
    }

    private void checkSelection(SelectionEvent e) {
        if (e != null) {
            if (e.getSource().toString().contains(TEXT_RADIO_DECLARATION)) {
                cbxExtractFuctionPointer.setSelection(false);
                cbxExtractFuctionReference.setSelection(false);
            } else if (e.getSource().toString().contains(TEXT_RADIO_POINTER)) {
                cbxExtractFunctionDeclaration.setSelection(false);
                cbxExtractFuctionReference.setSelection(false);
            } else if (e.getSource().toString().contains(TEXT_RADIO_REFERENCE)) {
                cbxExtractFunctionDeclaration.setSelection(false);
                cbxExtractFuctionPointer.setSelection(false);
            }
        }
    }

}
