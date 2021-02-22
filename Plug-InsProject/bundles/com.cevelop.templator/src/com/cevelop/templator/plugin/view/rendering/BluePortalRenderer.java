package com.cevelop.templator.plugin.view.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;


public class BluePortalRenderer {

    private static Font      font         = null;
    private static int       imageHeight;
    private final static int SMALLER_FONT = 4;
    private final static int OFFSET_TOP   = SMALLER_FONT / 2;

    public void draw(PaintEvent event, int x, int y, int height, String text) {
        createResources(height, event.gc.getDevice(), event.gc.getFont());
        Image image = ImageCache.get(ImageID.PORTAL_IN);
        int width = image.getBounds().width;
        event.gc.drawImage(image, x, y);
        event.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        event.gc.setFont(font);
        int descent = event.gc.getFontMetrics().getDescent();
        event.gc.drawText(text, x + width + 2, y - descent + OFFSET_TOP); // + 2 because centering
    }

    private void createResources(int height, Device eventDevice, Font eventFont) {
        if (imageHeight == height) {
            return; // nothing has to be done
        }
        imageHeight = height;
        createImage(height, eventDevice);
        createFont(height, eventDevice, eventFont);
    }

    private void createFont(int height, Device device, Font font) {
        FontData fontData = font.getFontData()[0];
        fontData.setHeight(height - SMALLER_FONT); // the font should be smaller else it could overlap
        if (BluePortalRenderer.font != null) {
            BluePortalRenderer.font.dispose();
        }
        BluePortalRenderer.font = new Font(device, fontData);
    }

    private void createImage(int height, Device device) {
        ImageCache.resizeImage(ImageID.PORTAL_IN, height);
    }
}
