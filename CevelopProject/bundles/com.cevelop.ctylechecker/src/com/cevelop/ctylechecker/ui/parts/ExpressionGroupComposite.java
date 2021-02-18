package com.cevelop.ctylechecker.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.cevelop.ctylechecker.domain.ResolutionHint;
import com.cevelop.ctylechecker.ui.util.DialogResult;
import com.cevelop.ctylechecker.ui.util.ExpressionItemArg;
import com.cevelop.ctylechecker.ui.util.ExpressionUIUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;


public class ExpressionGroupComposite extends Composite {

    private Text   txtGroupnameinput;
    private Button btnCancel;
    private Button btnSave;
    private Button btnAny;
    private Button btnAll;

    private TreeItem     item;
    private DialogResult dialogResult;
    private Combo        hintCombo;

    public ExpressionGroupComposite(Shell shell, TreeItem pItem, DialogResult pDialogResult) {
        super(shell, SWT.NONE);
        item = pItem;
        dialogResult = pDialogResult;
        setLayout(new GridLayout(1, false));

        Composite nameComposite = new Composite(this, SWT.NONE);
        GridLayout gl_nameComposite = new GridLayout(1, false);
        gl_nameComposite.marginWidth = 0;
        gl_nameComposite.marginHeight = 0;
        nameComposite.setLayout(gl_nameComposite);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblGroupName = new Label(nameComposite, SWT.NONE);
        lblGroupName.setText("Group Name");

        txtGroupnameinput = new Text(nameComposite, SWT.BORDER);
        txtGroupnameinput.setMessage("type group name");
        txtGroupnameinput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite matchComposite = new Composite(this, SWT.NONE);
        GridLayout gl_matchComposite = new GridLayout(1, false);
        gl_matchComposite.marginWidth = 0;
        gl_matchComposite.marginHeight = 0;
        matchComposite.setLayout(gl_matchComposite);
        matchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        matchComposite.setBounds(0, 0, 64, 64);

        Label lblMatchPolicy = new Label(matchComposite, SWT.NONE);
        lblMatchPolicy.setText("Match Policy");

        Composite hintComposite = new Composite(this, SWT.NONE);
        GridLayout gl_hintComposite = new GridLayout(1, false);
        gl_hintComposite.marginHeight = 0;
        gl_hintComposite.marginWidth = 0;
        hintComposite.setLayout(gl_hintComposite);
        hintComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblHint = new Label(hintComposite, SWT.NONE);
        lblHint.setText("Hint");

        hintCombo = new Combo(hintComposite, SWT.READ_ONLY);
        hintCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite radioComposite = new Composite(matchComposite, SWT.NONE);
        GridLayout gl_radioComposite = new GridLayout(2, false);
        gl_radioComposite.marginWidth = 0;
        gl_radioComposite.marginHeight = 0;
        radioComposite.setLayout(gl_radioComposite);

        btnAny = new Button(radioComposite, SWT.RADIO);
        btnAny.setText("Any");

        btnAll = new Button(radioComposite, SWT.RADIO);
        btnAll.setText("All");

        Composite buttonsComposite = new Composite(this, SWT.NONE);
        GridLayout gl_buttonsComposite = new GridLayout(2, false);
        gl_buttonsComposite.marginHeight = 0;
        gl_buttonsComposite.marginWidth = 0;
        buttonsComposite.setLayout(gl_buttonsComposite);
        buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1));
        buttonsComposite.setBounds(0, 0, 64, 64);

        btnSave = new Button(buttonsComposite, SWT.NONE);
        btnSave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnSave.setText("Save");

        btnCancel = new Button(buttonsComposite, SWT.NONE);
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnCancel.setText("Cancel");

        initializeFields();
        addListeners();
    }

    private void addListeners() {
        btnSave.addListener(SWT.MouseUp, (event) -> {
            readFields();
            dialogResult.setResult(SWT.OK);
            getShell().close();
        });

        btnCancel.addListener(SWT.MouseUp, (event) -> {
            dialogResult.setResult(SWT.CANCEL);
            getShell().close();
        });
    }

    private void readFields() {
        item.setText(ExpressionItemArg.NAME, txtGroupnameinput.getText());
        item.setText(ExpressionItemArg.MATCH, ExpressionUIUtil.convertMatchValue(btnAll.getSelection()));
        item.setText(ExpressionItemArg.HINT, hintCombo.getText());
    }

    private void initializeFields() {
        preInit();
        if (!item.getText(ExpressionItemArg.NAME).isEmpty()) {
            txtGroupnameinput.setText(item.getText(ExpressionItemArg.NAME));
            Boolean matchAll = ExpressionUIUtil.parseMatchValue(item.getText(ExpressionItemArg.MATCH));
            btnAny.setSelection(!matchAll);
            btnAll.setSelection(matchAll);
            for (int i = 0; i < hintCombo.getItemCount(); i++) {
                if (hintCombo.getItem(i).equals(item.getText(ExpressionItemArg.HINT))) {
                    hintCombo.select(i);
                    break;
                }
            }
        }
    }

    private void preInit() {
        hintCombo.add(ResolutionHint.NONE.name());
        hintCombo.add(ResolutionHint.PREFERED.name());
        btnAll.setSelection(true);
        hintCombo.select(0);
    }
}
