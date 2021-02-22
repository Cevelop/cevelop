package com.cevelop.ctylechecker.ui.util;

import org.eclipse.swt.SWT;


public class DialogResult {

    private int result = SWT.CANCEL;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
