package com.cevelop.templator.plugin.view.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionClickCallback;
import com.cevelop.templator.plugin.view.interfaces.IPortalClickCallback;
import com.cevelop.templator.plugin.view.listeners.ConnectionListener;


public class ConnectionCollection {

    private List<DirectConnection> directConnections;
    private List<PortalConnection> portalConnections;
    private boolean                portalsSorted;

    private Map<Point, Set<PortalConnection>> portalMap;

    public ConnectionCollection(Composite parent, IDirectConnectionClickCallback bezierCallback, IPortalClickCallback portalCallback) {
        directConnections = new ArrayList<>();
        portalConnections = new ArrayList<>();
        portalMap = new HashMap<>();
        ConnectionListener connectionListener = new ConnectionListener(parent, directConnections, portalConnections, bezierCallback, portalCallback);
        parent.addMouseListener(connectionListener);
        parent.addMouseMoveListener(connectionListener);
    }

    public List<DirectConnection> getDirectConnections() {
        return directConnections;
    }

    public List<PortalConnection> getPortalConnections() {
        return portalConnections;
    }

    public void clear() {
        for (DirectConnection directConnection : directConnections) {
            directConnection.getBezierCurve().dispose();
        }
        directConnections.clear();
        portalConnections.clear();
        portalsSorted = false;
    }

    private void sortPortals() {
        portalMap.clear();
        for (PortalConnection portalConnection : portalConnections) {
            Set<PortalConnection> set;
            Point key = portalConnection.getPortal().pos;
            if (portalMap.containsKey(key)) {
                set = portalMap.get(key);
            } else {
                set = new TreeSet<>();
            }
            set.add(portalConnection);
            portalMap.put(key, set);
        }
        portalsSorted = true;
    }

    public Map<Point, Set<PortalConnection>> getPortalMap() {
        if (!portalsSorted) {
            sortPortals();
        }
        return portalMap;
    }

    public void addConnection(Connection connection) {
        if (connection instanceof DirectConnection) {
            directConnections.add((DirectConnection) connection);
        } else if (connection instanceof PortalConnection) {
            portalConnections.add((PortalConnection) connection);
        }
    }

    public void resetBezierCurveWidth() {
        for (DirectConnection directConnection : directConnections) {
            directConnection.setActive(false);
        }
    }
}
