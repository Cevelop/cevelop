package com.cevelop.templator.plugin.view.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.components.ConnectionEnd;
import com.cevelop.templator.plugin.view.components.ConnectionStart;
import com.cevelop.templator.plugin.view.components.DirectConnection;
import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.components.ReverseDirectConnection;
import com.cevelop.templator.plugin.view.components.ReversePortalConnection;
import com.cevelop.templator.plugin.view.interfaces.INode;


public class TreeEntryCollectionNode implements INode {

    private List<Connection> connections = new ArrayList<>();
    private boolean          isRoot;
    private TreeEntry        entry;
    private boolean          dirty       = true;
    private int              x;

    // to create root node
    public TreeEntryCollectionNode(TreeEntry entry) {
        this.entry = entry;
        isRoot = true;
    }

    // to create any other node
    public TreeEntryCollectionNode(TreeEntry entry, TreeEntryCollectionNode parent, int nameIndex) {
        this.entry = entry;
        ConnectionStart start = new ConnectionStart(parent, nameIndex);
        ConnectionEnd end = new ConnectionEnd(this);
        addDirectConnection(start, end);
        parent.addReverseDirectConnection(start, end);
        isRoot = false;
    }

    public TreeEntry getEntry() {
        return entry;
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    public void addDirectConnection(ConnectionStart start, ConnectionEnd end) {
        connections.add(new DirectConnection(start, end));
    }

    public void addReverseDirectConnection(ConnectionStart start, ConnectionEnd end) {
        connections.add(new ReverseDirectConnection(start, end));
    }

    public void addPortalConnection(ConnectionStart start, ConnectionEnd end) {
        connections.add(new PortalConnection(start, end));
    }

    public void addReversePortalConnection(ConnectionStart start, ConnectionEnd end) {
        connections.add(new ReversePortalConnection(start, end));
    }

    public List<TreeEntryCollectionNode> getChildren() {
        return connections.stream().filter(p -> p instanceof ReverseDirectConnection && p.getStart().getEntry() == entry).map(p -> p.getEnd()
                .getTreeEntryNode()).collect(Collectors.toList());
    }

    public int getColumIdx() {
        return getMaxLengthToRoot();
    }

    @Override
    public List<Connection> getConnections() {
        return connections;
    }

    public List<DirectConnection> getDirectConnections() {
        return connections.stream().filter(p -> p instanceof DirectConnection).map(p -> (DirectConnection) p).collect(Collectors.toList());
    }

    public List<PortalConnection> getPortalConnections() {
        return connections.stream().filter(p -> p instanceof PortalConnection).map(p -> (PortalConnection) p).collect(Collectors.toList());
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public boolean hasConnections() {
        return !getDirectConnections().isEmpty();
    }

    private int getMaxLengthToRoot() {
        if (!dirty) {
            return x;
        }
        int max = 0;
        for (Connection connection : connections) {
            if (!(connection instanceof DirectConnection)) {
                continue; // don't go to the right
            }
            int pathLength = 1 + connection.getStart().getTreeEntryNode().getMaxLengthToRoot();
            if (pathLength > max) {
                max = pathLength;
            }
        }
        dirty = false;
        x = max;
        return max;
    }

    public void setDirty() {
        dirty = true;
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TreeEntryCollectionNode\n");
        sb.append("\t Entry: " + entry.getViewData().getTitle() + '\n');
        sb.append("\t Children:\n");
        for (TreeEntryCollectionNode child : getChildren()) {
            sb.append("\t\t " + child.entry.getViewData().getTitle() + '\n');
        }
        sb.append("\t Connections:\n");
        for (Connection connection : connections) {
            sb.append("\t\t start: " + connection.getStart().getEntry().getViewData().getTitle());
            sb.append(" , " + connection.getStart().getNameIndex() + '\n');
            sb.append("\t\t end: " + connection.getEnd().getEntry().getViewData().getTitle() + '\n');
            sb.append("\t\t portal: " + connection.isPortal());
        }
        return sb.toString();
    }
}
