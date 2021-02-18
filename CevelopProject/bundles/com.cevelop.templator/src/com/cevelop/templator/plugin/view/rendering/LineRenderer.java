package com.cevelop.templator.plugin.view.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;

import com.cevelop.templator.plugin.util.ColorPalette;
import com.cevelop.templator.plugin.view.components.Line;


public class LineRenderer {

    public void draw(PaintEvent event, Line line, int colorId, int lineStyle, int lineWidth) {
        event.gc.setLineStyle(lineStyle);
        event.gc.setLineWidth(lineWidth);
        event.gc.setForeground(ColorPalette.getColor(colorId));
        event.gc.drawLine(line.start.x, line.start.y, line.end.x, line.end.y);
        event.gc.setLineStyle(SWT.LINE_SOLID);
    }
}
