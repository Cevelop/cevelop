package com.cevelop.templator.plugin.view;

import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.components.TemplatorRectangle;


public final class ViewHelper {

    private ViewHelper() {}

    public static TemplatorRectangle calcTextRectangle(StyledText textField, IRegion region) {

        GC gc = new GC(textField);

        String contents = textField.getText();
        int lineHeight = textField.getLineHeight();

        String text = contents.substring(region.getOffset(), region.getOffset() + region.getLength());
        int stringWidth = gc.stringExtent(text).x;

        Point topLeft = textField.getLocationAtOffset(region.getOffset());
        TemplatorRectangle rect = new TemplatorRectangle(topLeft.x - 1, topLeft.y, stringWidth + 1, lineHeight - 1);

        gc.dispose();
        return rect;
    }

    public static void clearPaintListener(PaintListener listener, Composite parent) {
        if (listener != null) {
            parent.removePaintListener(listener);
        }
    }
}
