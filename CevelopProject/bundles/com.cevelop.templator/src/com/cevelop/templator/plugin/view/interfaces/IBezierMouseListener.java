package com.cevelop.templator.plugin.view.interfaces;

import org.eclipse.swt.events.MouseMoveListener;

import com.cevelop.templator.plugin.view.components.BezierCurve;


public interface IBezierMouseListener extends MouseMoveListener {

    boolean isBezierCurveHovered(BezierCurve bezierCurve, int x, int y);

    void findHoveredBezierCurve(int x, int y);
}
