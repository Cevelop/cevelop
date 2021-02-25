package com.cevelop.templator.plugin.view.rendering;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.cevelop.templator.plugin.view.components.BezierCurve;
import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.components.DirectConnection;
import com.cevelop.templator.plugin.view.components.Line;
import com.cevelop.templator.plugin.view.components.Portal;
import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.interfaces.INode;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public class ConnectionCalculator {

    private TreeEntry sourceComposite;
    private TreeEntry destinationComposite;

    // commented points are an example of possible coords for a half bezier path
    private Point            c1;                     // = new Point(120, 100);
    private Point            c2;                     // = new Point(140, 100);
    private Point            s;                      // = new Point(100, 100);
    private Point            e;                      // = new Point(140, 300);
    private static final int LINE_WIDTH         = 4;
    private static final int HOVER_LINE_WIDTH   = 11;
    private static final int PORTAL_MARGIN_LEFT = 2;

    public List<Connection> calculateConnections(INode node) {
        List<Connection> connections = new ArrayList<>();
        for (Connection connection : node.getConnections()) {
            this.sourceComposite = connection.getStart().getEntry();
            this.destinationComposite = connection.getEnd().getEntry();
            int rectOffset = connection.getStart().getRectangleOffset();
            int rectHeight = connection.getStart().getRectangleHeight();
            Display display = sourceComposite.getDisplay();
            if (connection instanceof DirectConnection) {
                DirectConnection directConnection = (DirectConnection) connection;
                BezierCurve bezierCurve = new BezierCurve();
                bezierCurve.sourceLine = calculateSourceVerticalLine(rectOffset, rectHeight);
                bezierCurve.bezierPath = calculatePath(display, rectOffset);
                bezierCurve.hoverPath = calculatePath(display, rectOffset, HOVER_LINE_WIDTH);
                bezierCurve.destinationLine = calculateDestinationVerticalLine();
                directConnection.setBezierCurve(bezierCurve);
                connections.add(directConnection);
            } else if (connection instanceof PortalConnection) {
                PortalConnection portalConnection = (PortalConnection) connection;
                Portal portal = new Portal();
                portal.sourceLine = calculateSourceVerticalLine(rectOffset, rectHeight);
                portal.pos = new Point(portal.sourceLine.start.x + PORTAL_MARGIN_LEFT, portal.sourceLine.start.y);
                portalConnection.setPortal(portal);
                connections.add(portalConnection);
            }
        }
        return connections;
    }

    private Path calculatePath(Display display, int rectOffset) {
        return calculatePath(display, rectOffset, 1);
    }

    private Path calculatePath(Display display, int rectOffset, int lineWidth) {
        Point start = sourceComposite.getLocation();
        Point end = destinationComposite.getLocation();
        start.y += rectOffset;
        start.x += sourceComposite.getSize().x + LINE_WIDTH;
        end.y += destinationComposite.getSize().y / 2;
        end.x -= LINE_WIDTH;

        start.y = limitY(start.y, sourceComposite);
        int width = (end.x - start.x);
        int height = (end.y - start.y);
        Path path = new Path(display);
        calcUpperCoords(start, end, width, height);
        if (lineWidth % 2 == 0) {
            ++lineWidth;
        }
        path = addPath(path, calcPartialPath(display));
        for (int i = 0; i < lineWidth / 2; i++) {
            path = addPath(path, calcPartialPath(display, +i));
            path = addPath(path, calcPartialPath(display, -i));
        }

        calcLowerCoords(start, end, width, height);
        path = addPath(path, calcPartialPath(display));
        for (int i = 0; i < lineWidth / 2; i++) {
            path = addPath(path, calcPartialPath(display, +i));
            path = addPath(path, calcPartialPath(display, -i));
        }
        return path;
    }

    private void calcUpperCoords(Point start, Point end, int width, int height) {
        s = start;
        e = new Point(start.x + width / 2, start.y + height / 2);
        c1 = new Point(s.x + width / 6, s.y);
        c2 = new Point(e.x - width / 6, s.y);
    }

    private void calcLowerCoords(Point start, Point end, int width, int height) {
        s = new Point(start.x + width / 2, start.y + height / 2);
        e = end;
        c1 = new Point(s.x + width / 6, e.y);
        c2 = new Point(e.x - width / 6, e.y);
    }

    private Path calcPartialPath(Display display) {
        return calcPartialPath(display, 0);
    }

    private Path addPath(Path mainPath, Path additionalPath) {
        mainPath.addPath(additionalPath);
        additionalPath.dispose();
        return mainPath;
    }

    private Path calcPartialPath(Display display, int offset) {
        Path path = new Path(display);
        path.moveTo(s.x + offset, s.y + offset);
        path.cubicTo(c1.x + offset, c1.y + offset, c2.x + offset, c2.y + offset, e.x + offset, e.y + offset);
        return path;
    }

    private Line calculateSourceVerticalLine(int rectOffset, int rectHeight) {
        Point midPoint = sourceComposite.getLocation();
        midPoint.y += rectOffset;
        midPoint.x += sourceComposite.getSize().x + LINE_WIDTH;

        int startY = midPoint.y - rectHeight / 2;
        int endY = midPoint.y + rectHeight / 2;

        startY = limitY(startY, sourceComposite);
        endY = limitY(endY, sourceComposite);

        Point start = new Point(midPoint.x, startY);
        Point end = new Point(midPoint.x, endY);
        return new Line(start, end);
    }

    private Line calculateDestinationVerticalLine() {
        Point start = destinationComposite.getLocation();
        Point end = destinationComposite.getLocation();

        start.x -= LINE_WIDTH;
        end.y += destinationComposite.getSize().y;
        end.x -= LINE_WIDTH;

        return new Line(start, end);
    }

    private int limitY(int pointY, Composite composite) {
        if (pointY < composite.getLocation().y) {
            pointY = composite.getLocation().y;
        }
        if (pointY > composite.getLocation().y + composite.getSize().y - 1) {
            pointY = composite.getLocation().y + composite.getSize().y - 1;
        }
        return pointY;
    }
}
