package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.util.CursorCache;
import com.cevelop.templator.plugin.util.CursorCache.CursorID;
import com.cevelop.templator.plugin.view.interfaces.ITreeViewController;


public class ResizableComposite extends Composite {

    private static final int TIMER_INTERVAL      = 20;
    private static final int AUTO_RESIZE_SPEED   = 7;
    private static final int AUTOSCROLL_MARGIN   = 10;
    private static final int RESIZE_BORDER_WIDTH = 20;

    protected Point minSize = new Point(260, 150);

    private Composite           parent;
    private ITreeViewController controller;

    private Cursor horizontalArrow;
    private Cursor verticalArrow;
    private Cursor diagonalArrow;

    private boolean horizontalResizePossible = false;
    private boolean verticalResizePossible   = false;
    private boolean horizontalResize         = false;
    private boolean verticalResize           = false;
    private boolean horizontalAutoScroll     = false;
    private boolean verticalAutoScroll       = false;

    public ResizableComposite(final Composite parent, final ITreeViewController controller, int style) {
        super(parent, style);

        this.parent = parent;
        this.controller = controller;

        this.horizontalArrow = CursorCache.get(CursorID.RESIZE_WE);
        this.verticalArrow = CursorCache.get(CursorID.RESIZE_NS);
        this.diagonalArrow = CursorCache.get(CursorID.RESIZE_NWSE);

        addMouseMoveListener(e -> onMouseMove(e));

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                endResize();
            }

            @Override
            public void mouseDown(MouseEvent e) {
                beginResize();
            }
        });

        addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseExit(MouseEvent e) {
                setCursor(null);
            }
        });
    }

    private void onMouseMove(MouseEvent e) {
        horizontalResizePossible = e.x > getSize().x - RESIZE_BORDER_WIDTH;
        verticalResizePossible = e.y > getSize().y - RESIZE_BORDER_WIDTH;

        if (!horizontalResize && !verticalResize) {
            if (horizontalResizePossible && verticalResizePossible) {
                setCursor(diagonalArrow);
            } else if (horizontalResizePossible) {
                setCursor(horizontalArrow);
            } else if (verticalResizePossible) {
                setCursor(verticalArrow);
            } else {
                setCursor(null);
            }
        } else if (horizontalResize || verticalResize) {
            onResize(e);
        }
    }

    private void beginResize() {
        horizontalResize = horizontalResizePossible;
        verticalResize = verticalResizePossible;

        if (horizontalResize || verticalResize) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    autoScroll();
                    if (horizontalResize || verticalResize) {
                        getDisplay().timerExec(TIMER_INTERVAL, this);
                    }
                }
            };
            getDisplay().timerExec(TIMER_INTERVAL, runnable);
        }
    }

    private void endResize() {
        horizontalResize = false;
        verticalResize = false;
        controller.reflow();
        setCursor(null);
    }

    private void onResize(MouseEvent e) {
        int right = getLocation().x + e.x;
        horizontalAutoScroll = right >= parent.getSize().x - AUTOSCROLL_MARGIN;
        if (horizontalResize && !horizontalAutoScroll) {
            int sizeX = e.x < minSize.x ? minSize.x : e.x;
            setSize(sizeX, getSize().y);
            parent.redraw();
        }

        int top = getLocation().y + e.y;
        verticalAutoScroll = top >= parent.getSize().y - AUTOSCROLL_MARGIN;
        if (verticalResize && !verticalAutoScroll) {
            int sizeY = e.y < minSize.y ? minSize.y : e.y;
            setSize(getSize().x, sizeY);
            parent.redraw();
        }
    }

    private void autoScroll() {
        if (horizontalAutoScroll) {
            resize(AUTO_RESIZE_SPEED, 0);
            controller.getForm().reflow(true);
            scroll(AUTO_RESIZE_SPEED, 0);
        }
        if (verticalAutoScroll) {
            resize(0, AUTO_RESIZE_SPEED);
            controller.getForm().reflow(true);
            scroll(0, AUTO_RESIZE_SPEED);
        }
    }

    private void resize(int x, int y) {
        Point size = getSize();
        size.x += x;
        size.y += y;
        setSize(size);
    }

    private void scroll(int x, int y) {
        Point origin = controller.getForm().getOrigin();
        origin.x += x;
        origin.y += y;
        controller.getForm().setOrigin(origin);
    }
}
