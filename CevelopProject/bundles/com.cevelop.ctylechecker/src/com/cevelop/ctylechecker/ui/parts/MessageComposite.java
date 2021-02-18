package com.cevelop.ctylechecker.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class MessageComposite extends Composite {

    private Text messageInputText;

    public MessageComposite(Composite parent) {
        super(parent, SWT.NONE);
        GridLayout gl_messageComposite = new GridLayout(1, false);
        gl_messageComposite.marginWidth = 0;
        gl_messageComposite.marginHeight = 0;
        this.setLayout(gl_messageComposite);
        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label messageTopLabel = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        messageTopLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblMessage = new Label(this, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        lblMessage.setText("Message");

        messageInputText = new Text(this, SWT.BORDER);
        messageInputText.setMessage("type message to be displayed on violation");
        messageInputText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    }

    public Text getMessageInputText() {
        return messageInputText;
    }

}
