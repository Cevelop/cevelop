package com.cevelop.templator.plugin.view.rendering;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.components.BezierCurve;
import com.cevelop.templator.plugin.view.components.ConnectionCollection;
import com.cevelop.templator.plugin.view.components.DirectConnection;
import com.cevelop.templator.plugin.view.components.Line;
import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public class ConnectionRenderer implements PaintListener {

    private static final int VERTICAL_LINE_WIDTH  = 2;
    private static final int BEZIER_WIDTH_INACIVE = 1;
    private static final int BEZIER_WIDTH_ACTIVE  = 5;
    private static final int MAX_SHOWN_PORTALS    = 3;

    private ConnectionCollection connectionCollection;

    private BezierRenderer     bezierRenderer = new BezierRenderer();
    private LineRenderer       lineRenderer   = new LineRenderer();
    private BluePortalRenderer portalRenderer = new BluePortalRenderer();

    public ConnectionRenderer(Composite composite, ConnectionCollection connectionCollection) {
        composite.addPaintListener(this);
        this.connectionCollection = connectionCollection;
    }

    @Override
    public void paintControl(PaintEvent e) {
        for (DirectConnection directConnection : connectionCollection.getDirectConnections()) {
            int colorId = directConnection.getColorId();
            BezierCurve bezierCurve = directConnection.getBezierCurve();
            drawSourceVerticalLine(e, bezierCurve.sourceLine, colorId);
            drawBezierConnection(e, bezierCurve.bezierPath, colorId, directConnection.isActive());
            drawDestinationVerticalLine(e, bezierCurve.destinationLine, colorId);
        }
        for (Set<PortalConnection> portalConnections : connectionCollection.getPortalMap().values()) {
            PortalConnection firstPortalConnection = portalConnections.iterator().next();
            TreeEntry entry = firstPortalConnection.getStart().getEntry();
            if (!entry.isMinimized()) {
                drawPortal(e, portalConnections);
            }
        }
    }

    private void drawSourceVerticalLine(PaintEvent e, Line line, int colorId) {
        lineRenderer.draw(e, line, colorId, SWT.LINE_SOLID, VERTICAL_LINE_WIDTH);
    }

    private void drawBezierConnection(PaintEvent e, Path curve, int colorId, boolean isClicked) {
        final int WIDTH = isClicked ? BEZIER_WIDTH_ACTIVE : BEZIER_WIDTH_INACIVE;
        bezierRenderer.draw(e, curve, colorId, WIDTH);
    }

    private void drawDestinationVerticalLine(PaintEvent e, Line line, int colorId) {
        lineRenderer.draw(e, line, colorId, SWT.LINE_SOLID, VERTICAL_LINE_WIDTH);
    }

    private void drawPortal(PaintEvent e, Set<PortalConnection> portalConnections) {
        String text = "";
        int x = 0;
        int y = 0;
        int portalHeight = 0;
        int count = 0;
        for (PortalConnection portalConnection : portalConnections) {
            if (text.isEmpty()) {
                x = portalConnection.getXPos();
                y = portalConnection.getYPos();
                portalHeight = portalConnection.getPortal().getHeight();
                text += portalConnection.getPortalNumber();
            } else if (count < MAX_SHOWN_PORTALS) {
                text += "," + portalConnection.getPortalNumber();
            } else {
                text += ",...";
                break;
            }
            ++count;
        }
        portalRenderer.draw(e, x, y, portalHeight, text);
    }
}
