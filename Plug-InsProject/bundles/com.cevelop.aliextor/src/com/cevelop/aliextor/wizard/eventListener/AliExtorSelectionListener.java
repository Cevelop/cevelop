package com.cevelop.aliextor.wizard.eventListener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.cevelop.aliextor.wizard.BaseWizardPage;


public class AliExtorSelectionListener extends SelectionAdapter {

    private BaseWizardPage page;

    public AliExtorSelectionListener(BaseWizardPage page) {
        this.page = page;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        page.setSelectionInRefactoring(e);
        page.setPageComplete();
    }

}
