package com.cevelop.templator.plugin.view.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.rendering.BorderPaintListener;


public class EntrySelectionHandler {

    private boolean borderAdded = false;
    private boolean hovering    = false;

    public EntrySelectionHandler(final Composite composite) {

        final BorderPaintListener borderPaintListener = new BorderPaintListener(composite);

        composite.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                if (!borderAdded) {
                    addBorder(composite, borderPaintListener);
                }
                borderPaintListener.resetAnimation();
                borderPaintListener.startAnimation(composite.getDisplay());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!hovering) {
                    removeBorder(composite, borderPaintListener);
                } else {
                    borderPaintListener.stopAnimation();
                    borderPaintListener.resetAnimation();
                }
            }
        });

        composite.addListener(SWT.MouseEnter, e -> {
            hovering = true;
            if (!composite.isFocusControl()) {
                addBorder(composite, borderPaintListener);
            }
        });
        composite.addListener(SWT.MouseExit, e -> {
            hovering = false;
            if (!composite.isFocusControl()) {
                removeBorder(composite, borderPaintListener);
            }
        });
    }

    private void removeBorder(final Composite widget, final BorderPaintListener borderPaintListener) {
        borderPaintListener.stopAnimation();
        widget.removePaintListener(borderPaintListener);
        widget.redraw();
        borderAdded = false;
    }

    private void addBorder(final Composite widget, final BorderPaintListener borderPaintListener) {
        borderPaintListener.resetAnimation();
        widget.addPaintListener(borderPaintListener);
        widget.redraw();
        borderAdded = true;
    }

}
