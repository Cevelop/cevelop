package com.cevelop.aliextor.wizard.eventListener;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import com.cevelop.aliextor.wizard.BaseWizardPage;


public class AliExtorKeyListener extends KeyAdapter {

    private BaseWizardPage page;

    public AliExtorKeyListener(BaseWizardPage page) {
        this.page = page;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        page.setTheUserInputInRefactoring();
        page.setPageComplete();
    }

}
