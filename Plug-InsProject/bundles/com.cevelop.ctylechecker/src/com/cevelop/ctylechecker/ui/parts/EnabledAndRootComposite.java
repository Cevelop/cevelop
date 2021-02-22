package com.cevelop.ctylechecker.ui.parts;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.IGrouping;


public class EnabledAndRootComposite extends Composite {

    private static final String ROOT_NAME = "ROOT";

    private Button btnEnabled;
    private Combo  rootCombo;

    public EnabledAndRootComposite(Composite parent, IConfiguration pConfig) {
        super(parent, SWT.NONE);
        GridLayout gl_enabledComposite = new GridLayout(3, false);
        gl_enabledComposite.marginWidth = 0;
        gl_enabledComposite.marginHeight = 0;
        this.setLayout(gl_enabledComposite);
        this.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnEnabled = new Button(this, SWT.CHECK);
        btnEnabled.setText("Enabled");
        btnEnabled.setSelection(true);

        Label lblRoot = new Label(this, SWT.NONE);
        lblRoot.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        lblRoot.setText("Root");

        rootCombo = new Combo(this, SWT.READ_ONLY);
        GridData gd_rootCombo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_rootCombo.widthHint = 180;
        gd_rootCombo.minimumWidth = 180;
        rootCombo.setLayoutData(gd_rootCombo);
        rootCombo.add(ROOT_NAME);
        rootCombo.select(0);
    }

    public Button getBtnEnabled() {
        return btnEnabled;
    }

    public Combo getRootCombo() {
        return rootCombo;
    }

    public void initializeRootCombo(IGrouping ruleRoot) {
        if (ruleRoot != null) {
            String[] items = rootCombo.getItems();
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(ruleRoot.getName())) {
                    rootCombo.select(i);
                    break;
                }
            }
        }
    }

    public void populateRootComboField(IConfiguration pConfig) {
        if (pConfig != null) {
            Collection<IGrouping> groupings = pConfig.getActiveStyleguide().getGroupings();
            int index = 1;
            for (IGrouping grouping : groupings) {
                rootCombo.add(grouping.getName());
                rootCombo.setData(index + "", grouping.getId());
                index++;
            }
        }
    }

}
