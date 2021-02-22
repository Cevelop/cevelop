package com.cevelop.templator.plugin.view.listeners;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.util.CursorCache;
import com.cevelop.templator.plugin.util.CursorCache.CursorID;
import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.components.BezierCurve;
import com.cevelop.templator.plugin.view.components.DirectConnection;
import com.cevelop.templator.plugin.view.components.Portal;
import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.interfaces.IBezierMouseListener;
import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IPortalClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IPortalMouseListener;


/**
 * Listener for the Bezier curves and the blue portals
 */
public class ConnectionListener implements MouseListener, MouseMoveListener, IPortalMouseListener, IBezierMouseListener {

    private boolean   isBezierCurveHovered;
    private boolean   isPortalHovered;
    private Composite composite;
    private Cursor    cursorArrow;
    private Cursor    cursorPortalHover;
    private Cursor    cursorHandPointer;
    private Cursor    cursorPortalTravel;

    private boolean isMacOS;

    private List<DirectConnection> directConnections;
    private List<PortalConnection> portalConnections;

    private DirectConnection      hoveredDirectConnection;
    private Set<PortalConnection> hoveredPortalConnections;

    private IDirectConnectionClickCallback directConnectionCallback;
    private IPortalClickCallback           portalCallback;

    public ConnectionListener(Composite composite, List<DirectConnection> directConnections, List<PortalConnection> portalConnections,
                              IDirectConnectionClickCallback bezierCallback, IPortalClickCallback portalCallback) {
        this.composite = composite;
        this.directConnections = directConnections;
        this.portalConnections = portalConnections;
        this.directConnectionCallback = bezierCallback;
        this.portalCallback = portalCallback;
        cursorArrow = CursorCache.get(CursorID.ARROW);
        cursorHandPointer = CursorCache.get(CursorID.HAND_POINTER);
        cursorPortalHover = CursorCache.get(CursorID.PORTAL_HOVER);
        cursorPortalTravel = CursorCache.get(CursorID.PORTAL_TRAVEL);
        isBezierCurveHovered = false;
        isPortalHovered = false;
        hoveredPortalConnections = new TreeSet<>();
        isMacOS = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
    }

    @Override
    public void mouseMove(MouseEvent e) {
        findHoveredPortals(e.x, e.y);
        if (isPortalHovered) {
            composite.setCursor(cursorPortalHover);
        } else {
            findHoveredBezierCurve(e.x, e.y);
            if (isBezierCurveHovered) {
                composite.setCursor(cursorHandPointer);
            } else {
                composite.setCursor(cursorArrow);
            }
        }
    }

    @Override
    public void mouseDown(MouseEvent e) {
        if (isPortalHovered) {
            composite.setCursor(cursorPortalTravel);
            portalCallback.bluePortalClicked(hoveredPortalConnections);
            isPortalHovered = false;
        } else if (isBezierCurveHovered) {
            directConnectionCallback.directConnectionClicked(hoveredDirectConnection);
            isBezierCurveHovered = false;
        } else {
            composite.setCursor(cursorArrow);
        }
    }

    @Override
    public void mouseUp(MouseEvent e) {
        if (isPortalHovered) {
            composite.setCursor(cursorPortalHover);
        } else if (isBezierCurveHovered) {
            composite.setCursor(cursorHandPointer);
        } else {
            composite.setCursor(cursorArrow);
        }
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {}

    @Override
    public void findHoveredBezierCurve(int x, int y) {
        isBezierCurveHovered = false;
        for (DirectConnection directConnection : directConnections) {
            BezierCurve bezierCurve = directConnection.getBezierCurve();
            if (isBezierCurveHovered(bezierCurve, x, y)) {
                hoveredDirectConnection = directConnection;
                isBezierCurveHovered = true;
                return;
            }
        }
    }

    @Override
    public boolean isBezierCurveHovered(BezierCurve bezierCurve, int x, int y) {
        GC gc = new GC(bezierCurve.bezierPath.getDevice());
        boolean isHovered;
        if (isMacOS) {
            isHovered = bezierCurve.hoverPath.contains(x, y, gc, false);
        } else {
            isHovered = bezierCurve.hoverPath.contains(x, y, gc, true);
        }
        gc.dispose();
        return isHovered;
    }

    @Override
    public void findHoveredPortals(int x, int y) {
        hoveredPortalConnections.clear();
        isPortalHovered = false;
        for (PortalConnection portalConnection : portalConnections) {
            Portal portal = portalConnection.getPortal();
            if (isPortalHovered(portal, x, y)) {
                isPortalHovered = true;
                hoveredPortalConnections.add(portalConnection);
            }
        }
    }

    @Override
    public boolean isPortalHovered(Portal portal, int x, int y) {
        Rectangle bounds = ImageCache.get(ImageID.PORTAL_IN).getBounds();
        int left = portal.pos.x;
        int right = left + bounds.width;
        int top = portal.pos.y;
        int bottom = top + bounds.height;
        return x >= left && x <= right && y >= top && y <= bottom;
    }
}
