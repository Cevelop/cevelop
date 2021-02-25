package com.cevelop.ctylechecker.ui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public abstract class EnterKeyListener implements Listener {

    @Override
    public void handleEvent(Event event) {
        if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.TRAVERSE_RETURN) {
            handle(event);
        }
    }

    public abstract void handle(Event event);
}
