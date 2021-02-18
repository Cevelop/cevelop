package com.cevelop.conanator.preferences;

import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cevelop.conanator.models.Remote;


public class EditRemoteDialog extends TitleAreaDialog {

    private boolean      isNew;
    private List<Remote> existingRemotes;
    private Remote       remote;
    private Text         nameText;
    private Text         urlText;
    private Combo        sslCombo;

    public EditRemoteDialog(Shell parentShell, List<Remote> existingRemotes) {
        this(parentShell, existingRemotes, new Remote());
        this.isNew = true;
    }

    public EditRemoteDialog(Shell parentShell, List<Remote> existingRemotes, Remote remote) {
        super(parentShell);
        this.existingRemotes = existingRemotes;
        this.remote = remote;
    }

    @Override
    public void create() {
        super.create();

        if (isNew) {
            getShell().setText("Add Remote");
            setTitle("Enter remote details");
        } else {
            getShell().setText("Edit Remote");
            setTitle("Edit remote details");
        }

        nameText.setText(remote.getName());
        urlText.setText(remote.getUrl());
        sslCombo.setText(remote.getSsl());
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        createRemoteNameEditor(container);
        createRemoteURLEditor(container);
        createRemoteSSLEditor(container);

        return area;
    }

    private void createRemoteNameEditor(Composite parent) {
        Label nameLbl = new Label(parent, SWT.NONE);
        nameLbl.setText("Remote Name:");

        nameText = new Text(parent, SWT.BORDER);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameText.setMessage("conan-center");
        nameText.addModifyListener(e -> validate());
    }

    private void createRemoteURLEditor(Composite parent) {
        Label nameLbl = new Label(parent, SWT.NONE);
        nameLbl.setText("Remote URL:");

        urlText = new Text(parent, SWT.BORDER);
        urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        urlText.setMessage("https://conan.bintray.com");
        urlText.addModifyListener(e -> validate());
    }

    private void createRemoteSSLEditor(Composite parent) {
        Label nameLbl = new Label(parent, SWT.NONE);
        nameLbl.setText("Verify SSL:");

        sslCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        sslCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sslCombo.setItems("True", "False");
        sslCombo.select(0);
    }

    private void validate() {
        Button okBtn = getButton(IDialogConstants.OK_ID);
        String message = null;
        int messageType = IMessageProvider.ERROR;

        if (nameText.getText().isEmpty()) {
            message = "Remote Name is required";
        } else if (!isValidName(nameText.getText())) {
            message = "Remote Name can contain only these characters: [a-zA-Z_0-9-.]";
        } else if (urlText.getText().isEmpty()) {
            message = "Remote URL is required";
        } else if (!isValidURL(urlText.getText())) {
            message = "Remote URL must be a valid URL";
        } else if (isNew && hasDuplicateAttribute(nameText.getText(), Remote::getName)) {
            message = "A Remote with the given Name already exists\n" + "Only the last one of these Remotes will be considered";
            messageType = IMessageProvider.WARNING;
        } else if (isNew && hasDuplicateAttribute(urlText.getText(), Remote::getUrl)) {
            message = "A Remote with the given URL already exists";
            messageType = IMessageProvider.WARNING;
        }

        okBtn.setEnabled(message == null || messageType != IMessageProvider.ERROR);
        setMessage(message, messageType);
    }

    private boolean isValidName(String name) {
        return Pattern.compile("^[\\w-.]++$").matcher(name).matches();
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasDuplicateAttribute(String attribute, Function<Remote, String> getAttribute) {
        for (Remote r : existingRemotes) {
            if (getAttribute.apply(r).equals(attribute)) return true;
        }

        return false;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void okPressed() {
        remote.setName(nameText.getText());
        remote.setUrl(urlText.getText());
        remote.setSsl(sslCombo.getText());
        super.okPressed();
    }

    public Remote getRemote() {
        return remote;
    }
}
