package com.cevelop.aliextor.wizard;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.wizard.eventListener.AliExtorKeyListener;
import com.cevelop.aliextor.wizard.eventListener.AliExtorSelectionListener;


public abstract class BaseWizardPage extends UserInputWizardPage {

    protected Text                      theUserInput;
    protected Composite                 container;
    protected Composite                 baseContainer;
    protected AliExtorSelectionListener selectionListener;

    public BaseWizardPage(String name) {
        super(name);
        selectionListener = new AliExtorSelectionListener(this);
    }

    @Override
    public void createControl(Composite parent) {
        setMessage("Please choose a refactoring type!");

        baseContainer = new Composite(parent, SWT.NONE);
        baseContainer.setLayout(new GridLayout());

        container = new Composite(baseContainer, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label label1 = new Label(container, SWT.NONE);
        label1.setText("Name of Alias");

        theUserInput = new Text(container, SWT.BORDER | SWT.SINGLE);
        theUserInput.setText("alias");
        theUserInput.addKeyListener(new AliExtorKeyListener(this));

        setTheUserInputInRefactoring();

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        theUserInput.setLayoutData(gd);

        createDummyLabel();
        createDummyLabel();
    }

    protected Button createRadioButton(Button btn, String text, int type) {
        // Dummy Label to shift the button
        createDummyLabel();

        btn = new Button(container, type);
        btn.setText(text);

        btn.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        btn.addSelectionListener(selectionListener);

        return btn;
    }

    protected void createDummyLabel() {
        new Label(container, SWT.NONE).setText("");
    }

    public void setPageComplete() {
        setPageComplete(canFlipToNextPage());
    }

    @Override
    public boolean canFlipToNextPage() {
        return !theUserInput.getText().isEmpty();
    }

    public void setTheUserInputInRefactoring() {
        ((AliExtorRefactoring) getRefactoring()).setTheUserInput(theUserInput.getText());
    }

    public String getTheUserInput() {
        return theUserInput.getText();
    }

    abstract public void setSelectionInRefactoring(SelectionEvent e);

}
