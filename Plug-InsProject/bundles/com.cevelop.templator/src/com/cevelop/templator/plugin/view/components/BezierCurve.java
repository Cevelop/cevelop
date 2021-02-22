package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.graphics.Path;


public class BezierCurve {

    public Line sourceLine;
    public Path bezierPath;
    public Path hoverPath;
    public Line destinationLine;

    public void dispose() {
        bezierPath.dispose();
        hoverPath.dispose();
    }
}
