package com.cevelop.templator.plugin.view.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Path;

import com.cevelop.templator.plugin.util.ColorPalette;


public class BezierRenderer {

    private static final int LINE_STYLE = SWT.LINE_SOLID;

    public void draw(PaintEvent event, Path path, int colorId, int width) {
        event.gc.setLineStyle(LINE_STYLE);
        event.gc.setLineWidth(width);
        event.gc.setForeground(ColorPalette.getColor(colorId));
        event.gc.drawPath(path);
        event.gc.setLineStyle(SWT.LINE_SOLID);
    }
}
