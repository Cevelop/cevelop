package com.cevelop.ctylechecker.ui.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.domain.IGrouping;
import com.cevelop.ctylechecker.ui.AbstractCtylecheckerComposite;
import com.cevelop.ctylechecker.ui.listener.EnterKeyListener;
import com.cevelop.ctylechecker.ui.util.DialogResult;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;


public class GroupingComposite extends AbstractCtylecheckerComposite {

    public Text    text;
    public Button  btnEnabled;
    private Button btnNewGroup;
    private Button btnCancel;
    private Label  labelGroupName;

    /**
     * @param parent
     * The composite
     * @param pConfig
     * The configuration
     * @param pElement
     * The style element
     * @param pDialogResult
     * The result of the dialog
     * 
     * @wbp.parser.constructor
     */
    public GroupingComposite(Composite parent, IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialogResult) {
        super(parent, SWT.NONE);
        createContents(pConfig, pElement, pDialogResult);
    }

    private void createContents(IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialogResult) {
        setLayout(new GridLayout(1, false));

        Composite enabledComposite = new Composite(this, SWT.NONE);
        enabledComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnEnabled = new Button(enabledComposite, SWT.CHECK);
        btnEnabled.setBounds(0, 0, 93, 16);
        btnEnabled.setText("Enabled");
        btnEnabled.setSelection(true);

        Composite inputComposite = new Composite(this, SWT.NONE);
        inputComposite.setLayout(new FillLayout(SWT.VERTICAL));
        inputComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        labelGroupName = new Label(inputComposite, SWT.NONE);
        labelGroupName.setText("Group Name");

        text = new Text(inputComposite, SWT.BORDER);
        text.setMessage("type new group name");

        Composite buttonsComposite = new Composite(this, SWT.NONE);
        buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));

        btnNewGroup = new Button(buttonsComposite, SWT.NONE);
        if (pElement != null) {
            btnNewGroup.setText("Save Group");
            IGrouping existingGrouping = (IGrouping) pElement;
            text.setText(existingGrouping.getName());
            btnEnabled.setSelection(existingGrouping.isEnabled());
        } else {
            btnNewGroup.setText("New Group");
        }

        btnCancel = new Button(buttonsComposite, SWT.NONE);
        btnCancel.setText("Cancel");

        setupListeners(pConfig, pElement, pDialogResult);
    }

    private void setupListeners(IConfiguration pConfig, ICtyleElement pElement, DialogResult pDialogResult) {
        Listener newGroupListener = (event) -> {

            IGrouping newGrouping = getRegistry().getGroupingService().createGroup(text.getText(), btnEnabled.getSelection());
            if (pElement != null) {
                IGrouping existingGrouping = (IGrouping) pElement;
                existingGrouping.setName(text.getText());
                existingGrouping.isEnabled(btnEnabled.getSelection());
            } else {
                pConfig.getActiveStyleguide().addGrouping(newGrouping);
            }
            pDialogResult.setResult(SWT.OK);
            getShell().close();
        };

        Listener cancelListener = (event) -> {
            pDialogResult.setResult(SWT.CANCEL);
            getShell().close();
        };

        EnterKeyListener enterKeyOnNewGroupListener = new EnterKeyListener() {

            @Override
            public void handle(Event event) {
                newGroupListener.handleEvent(event);
            }
        };

        EnterKeyListener enterKeyOnCancelListener = new EnterKeyListener() {

            @Override
            public void handle(Event event) {
                cancelListener.handleEvent(event);
            }
        };

        text.addListener(SWT.KeyUp, enterKeyOnNewGroupListener);

        btnNewGroup.addListener(SWT.MouseUp, newGroupListener);
        btnNewGroup.addListener(SWT.Traverse, enterKeyOnNewGroupListener);

        btnCancel.addListener(SWT.MouseUp, cancelListener);
        btnCancel.addListener(SWT.Traverse, enterKeyOnCancelListener);
    }
}
