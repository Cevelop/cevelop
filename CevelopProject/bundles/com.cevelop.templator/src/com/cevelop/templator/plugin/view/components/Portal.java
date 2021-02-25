package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.graphics.Point;


public class Portal {

    public Line  sourceLine;
    public Point pos;

    public int getHeight() {
        return sourceLine.end.y - sourceLine.start.y;
    }
}
