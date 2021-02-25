package com.cevelop.templator.plugin.view.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.components.TemplatorRectangle;
import com.cevelop.templator.plugin.view.interfaces.IRectangleHoverListener;


public class RectangleHoverListener implements MouseMoveListener {

    private Composite                parent;
    private List<TemplatorRectangle> rects;

    private Cursor handCursor;

    private IRectangleHoverListener hoverListener;

    private List<Integer> hoveredRects = new ArrayList<>();

    public RectangleHoverListener(Composite parent, List<TemplatorRectangle> rects, IRectangleHoverListener hoverListener) {
        this.parent = parent;
        this.rects = rects;
        this.hoverListener = hoverListener;

        this.handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);

        parent.addMouseMoveListener(this);

        parent.addMouseTrackListener(new MouseTrackAdapter() {

            @Override
            public void mouseExit(MouseEvent e) {
                for (int rectIdx : hoveredRects) {
                    cursorLeftRect(rectIdx);
                }
                hoveredRects.clear();
            }
        });
    }

    private void cursorEnteredRect(int index) {
        parent.setCursor(handCursor);
        hoverListener.enterRectangle(index);
    }

    private void cursorLeftRect(int index) {
        if (hoveredRects.size() == 0) {
            parent.setCursor(null);
        }
        hoverListener.leaveRectangle(index);
    }

    @Override
    public void mouseMove(MouseEvent e) {

        List<Integer> newHoveredRects = new ArrayList<>();

        int index = 0;
        for (TemplatorRectangle rect : rects) {
            if (rect.contains(e.x, e.y)) {
                newHoveredRects.add(index);
            }
            index++;
        }

        List<Integer> enterRects = new ArrayList<>(newHoveredRects);
        enterRects.removeAll(hoveredRects);

        List<Integer> leaveRects = new ArrayList<>(hoveredRects);
        leaveRects.removeAll(newHoveredRects);

        hoveredRects = newHoveredRects;

        for (int leaveIndex : leaveRects) {
            cursorLeftRect(leaveIndex);
        }

        for (int enterIndex : enterRects) {
            cursorEnteredRect(enterIndex);
        }
    }

}
