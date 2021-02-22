package com.cevelop.templator.plugin.view.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import com.cevelop.templator.plugin.util.ColorPalette;
import com.cevelop.templator.plugin.view.ViewHelper;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.interfaces.IRectangleHoverListener;
import com.cevelop.templator.plugin.view.interfaces.ISubNameClickCallback;
import com.cevelop.templator.plugin.view.interfaces.ISubNameClickCallback.ClickAction;
import com.cevelop.templator.plugin.view.listeners.RectangleHoverListener;
import com.cevelop.templator.plugin.view.rendering.RectanglePaintListener;


public class RectangleCollection extends MouseAdapter implements IRectangleHoverListener {

    private static final int SWT_LMB = 1;
    private static final int SWT_RMB = 3;

    private StyledText               textField;
    private List<Integer>            depthOrders;
    private List<TemplatorRectangle> rects;
    private Map<Integer, IRegion>    names;
    private ISubNameClickCallback    clickCallback;

    private Map<Integer, StyleRange[]> originalStyleRanges = new HashMap<>();

    public RectangleCollection(StyledText textField, Map<Integer, IRegion> names, ISubNameClickCallback clickCallback) {

        this.textField = textField;
        this.names = names;
        this.clickCallback = clickCallback;

        calcRectsAndOrders();

        new RectanglePaintListener(textField, rects);
        new RectangleHoverListener(textField, rects, this);

        textField.addMouseListener(this);

        for (Entry<Integer, IRegion> name : names.entrySet()) {
            int offset = name.getValue().getOffset();
            int length = name.getValue().getLength();
            StyleRange[] styleRanges = textField.getStyleRanges(offset, length);
            originalStyleRanges.put(name.getKey(), styleRanges);
        }
    }

    private TreeSet<Integer> rectOrder = new TreeSet<>((i1, i2) -> depthOrders.get(i1) - depthOrders.get(i2));

    @Override
    public void enterRectangle(int rectIndex) {
        if (rectOrder.size() > 0) {
            if (depthOrders.get(rectOrder.last()) < depthOrders.get(rectIndex)) {
                cleanRect(rectOrder.last());
            }
        }

        rectOrder.add(rectIndex);
        paintRect(rectIndex);
    }

    @Override
    public void leaveRectangle(int rectIndex) {
        cleanRect(rectIndex);
        rectOrder.remove(rectIndex);

        if (rectOrder.size() > 0) {
            paintRect(rectOrder.last());
        }
    }

    private void paintRect(int rectIndex) {
        StyleRange[] styleRanges = originalStyleRanges.get(rectIndex);
        StyleRange[] newStyleRanges = new StyleRange[styleRanges.length];

        int index = 0;
        for (StyleRange style : styleRanges) {
            StyleRange styleCopy = (StyleRange) style.clone();
            TemplatorRectangle rect = rects.get(rectIndex);
            if (rect.getState() == TemplatorRectangleState.ACTIVE) {
                styleCopy.background = ColorPalette.getBrightColor(rect.getColorId());
            } else {
                styleCopy.background = ColorPalette.getHoverColor();
            }
            newStyleRanges[index++] = styleCopy;
        }

        IRegion name = names.get(rectIndex);
        textField.replaceStyleRanges(name.getOffset(), name.getLength(), newStyleRanges);
    }

    private void cleanRect(int rectIndex) {
        StyleRange[] styleRanges = originalStyleRanges.get(rectIndex);

        IRegion name = names.get(rectIndex);
        textField.replaceStyleRanges(name.getOffset(), name.getLength(), styleRanges);
    }

    @Override
    public void mouseUp(MouseEvent e) {
        TemplatorRectangle selectedRect = findClickedRect(e);
        int clickedNameIndex = rects.indexOf(selectedRect);

        if (selectedRect != null) {
            switch (e.button) {
            case SWT_LMB:
                if ((e.stateMask & SWT.CONTROL) == SWT.CONTROL) {
                    clickCallback.nameClicked(clickedNameIndex, ClickAction.CTRL_LEFT_CLICK);
                } else {
                    clickCallback.nameClicked(clickedNameIndex, ClickAction.LEFT_CLICK);
                }
                break;
            default:
            case SWT_RMB:
                clickCallback.nameClicked(clickedNameIndex, ClickAction.RIGHT_CLICK);
                break;
            }
        }
    }

    private TemplatorRectangle findClickedRect(MouseEvent e) {
        TemplatorRectangle selectedRect = null;
        int selectedRectOrder = -1;

        for (int i = 0; i < rects.size(); i++) {
            TemplatorRectangle rect = rects.get(i);
            if (rect.contains(e.x, e.y)) {
                int newOrder = depthOrders.get(i);
                if (selectedRectOrder == -1 || newOrder > selectedRectOrder) {
                    selectedRect = rect;
                    selectedRectOrder = newOrder;
                }
            }
        }
        return selectedRect;
    }

    private void calcRectsAndOrders() {
        rects = new ArrayList<>();
        depthOrders = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            IRegion textSegment = names.get(i);
            TemplatorRectangle rect = ViewHelper.calcTextRectangle(textField, textSegment);

            int order = calcOrder(rect);

            depthOrders.add(order);
            rects.add(rect);
        }
    }

    private int calcOrder(TemplatorRectangle rect) {
        int order = 0;
        for (int j = 0; j < rects.size(); j++) {
            TemplatorRectangle previousRect = rects.get(j);

            boolean sameLine = previousRect.getY() == rect.getY();
            boolean leftEdgeInside = rect.getX() >= previousRect.getX();
            boolean rightEdgeInside = rect.getX() + rect.getWidth() <= previousRect.getX() + previousRect.getWidth();
            if (sameLine && leftEdgeInside && rightEdgeInside) {
                order = depthOrders.get(j) + 1;
            }
        }
        return order;
    }

    public TemplatorRectangle get(int index) {
        return rects.get(index);
    }

    public int size() {
        return rects.size();
    }

    public void updateRectangles() {
        textField.redraw();
        textField.update();
    }
}
