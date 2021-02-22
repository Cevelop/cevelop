package com.cevelop.templator.plugin.view.rendering;

import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.util.ColorPalette;
import com.cevelop.templator.plugin.util.SettingsCache;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;


public class RectanglePaintListener implements PaintListener {

    private List<TemplatorRectangle> rects;

    public RectanglePaintListener(Composite parent, List<TemplatorRectangle> collection) {
        this.rects = collection;
        parent.addPaintListener(this);
    }

    private void drawRectangle(PaintEvent event, Rectangle rect, Color color) {
        event.gc.setForeground(color);
        event.gc.drawRectangle(rect);
    }

    @Override
    public void paintControl(PaintEvent e) {
        for (TemplatorRectangle rect : rects) {
            if (rect.getState() == TemplatorRectangleState.ACTIVE) {
                drawRectangle(e, rect.getRectangle(), ColorPalette.getColor(rect.getColorId()));
            } else if (SettingsCache.areAllRectanglesVisible()) {
                drawRectangle(e, rect.getRectangle(), ColorPalette.getRectangleColor());
            }
        }
    }
}
