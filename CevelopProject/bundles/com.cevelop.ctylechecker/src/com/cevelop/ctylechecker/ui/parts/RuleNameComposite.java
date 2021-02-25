package com.cevelop.ctylechecker.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class RuleNameComposite extends Composite {

    private Text ruleNameText;

    public RuleNameComposite(Composite parent) {
        super(parent, SWT.NONE);
        this.setLayout(new FillLayout(SWT.VERTICAL));
        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblNewLabel = new Label(this, SWT.NONE);
        lblNewLabel.setText("Rule Name");

        ruleNameText = new Text(this, SWT.BORDER);
        ruleNameText.setMessage("type rule name");
        ruleNameText.setBounds(0, 0, 76, 21);
    }

    public Text getRuleNameText() {
        return ruleNameText;
    }

}
