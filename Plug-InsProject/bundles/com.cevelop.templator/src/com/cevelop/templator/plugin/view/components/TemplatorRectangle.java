package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.graphics.Rectangle;


public class TemplatorRectangle {

    private Rectangle               rectangle;
    private TemplatorRectangleState state;
    private int                     colorId;

    public TemplatorRectangle(int x, int y, int width, int height) {
        rectangle = new Rectangle(x, y, width, height);
        state = TemplatorRectangleState.IDLE;
    }

    public int getX() {
        return rectangle.x;
    }

    public int getY() {
        return rectangle.y;
    }

    public int getWidth() {
        return rectangle.width;
    }

    public int getHeigth() {
        return rectangle.height;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public boolean contains(int x, int y) {
        return rectangle.contains(x, y);
    }

    public void setState(TemplatorRectangleState state) {
        this.state = state;
    }

    public TemplatorRectangleState getState() {
        return state;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public enum TemplatorRectangleState {
        IDLE, ACTIVE;
    }
}
