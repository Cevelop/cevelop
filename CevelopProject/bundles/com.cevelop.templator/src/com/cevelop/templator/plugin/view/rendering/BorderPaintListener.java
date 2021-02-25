package com.cevelop.templator.plugin.view.rendering;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class BorderPaintListener implements PaintListener {

    private static final int   TIMER_INTERVAL  = 10;
    private static final float ALPHA_INCREMENT = 0.4f;
    private static final int   ALPHA_INITIAL   = 120;

    private int     alpha         = ALPHA_INITIAL;
    private boolean rising        = false;
    private boolean stopAnimation = false;

    private long lastTime;

    private Composite widget;

    public BorderPaintListener(Composite widget) {
        this.widget = widget;
    }

    @Override
    public void paintControl(PaintEvent event) {

        Composite parent = (Composite) event.getSource();

        Color gray = event.gc.getDevice().getSystemColor(SWT.COLOR_DARK_GRAY);

        event.gc.setAlpha(alpha);
        event.gc.setForeground(gray);
        event.gc.setLineWidth(3);
        event.gc.drawRectangle(new Rectangle(0, 0, parent.getBounds().width - 5, parent.getBounds().height - 5));
    }

    private boolean animate() {

        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastTime;

        if (rising) {
            alpha += ALPHA_INCREMENT * duration;
        } else {
            alpha -= ALPHA_INCREMENT * duration;
        }

        if (alpha > 255) {
            alpha = 255;
        } else if (alpha < 0) {
            alpha = 0;
        }

        if (!rising && alpha <= 0) {
            rising = true;
        }

        widget.redraw();

        lastTime = currentTime;

        return alpha < 255;
    }

    public void resetAnimation() {
        rising = false;
        alpha = ALPHA_INITIAL;
    }

    public void stopAnimation() {
        stopAnimation = true;
        resetAnimation();
    }

    public void startAnimation(final Display display) {
        lastTime = System.currentTimeMillis();
        stopAnimation = false;
        resetAnimation();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (!stopAnimation && animate()) {
                    display.timerExec(TIMER_INTERVAL, this);
                }
            }
        };
        display.timerExec(TIMER_INTERVAL, runnable);
    }
}
