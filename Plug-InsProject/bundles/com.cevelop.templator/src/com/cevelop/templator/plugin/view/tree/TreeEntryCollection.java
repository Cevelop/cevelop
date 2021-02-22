package com.cevelop.templator.plugin.view.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.components.ConnectionEnd;
import com.cevelop.templator.plugin.view.components.ConnectionStart;
import com.cevelop.templator.plugin.view.components.TemplatorRectangle.TemplatorRectangleState;
import com.cevelop.templator.plugin.view.interfaces.INode;
import com.cevelop.templator.plugin.view.interfaces.INodeCollection;
import com.cevelop.templator.plugin.viewdata.ViewData;


public class TreeEntryCollection implements INodeCollection {

    private List<TreeSet<TreeEntry>>                entries    = new ArrayList<>();
    private Map<TreeEntry, TreeEntryCollectionNode> entryNodes = new HashMap<>();
    private int                                     colorIndex = 0;

    public TreeEntryCollectionNode getCommonParentNode(ViewData data, TreeEntry parent) {
        for (TreeEntryCollectionNode node : entryNodes.get(parent).getChildren()) {
            if (node.getEntry().getViewData().equals(data)) {
                return node;
            }
        }
        return null;
    }

    public TreeEntryCollectionNode getCommonNodeInNextLevels(ViewData data, TreeEntry parent) {
        TreeEntryCollectionNode parentNode = entryNodes.get(parent);
        for (TreeEntryCollectionNode node : entryNodes.values()) {
            if (node.getEntry().getViewData().equals(data)) {
                if (node.getColumIdx() > parentNode.getColumIdx()) {
                    return node;
                }
            }
        }
        return null;
    }

    public TreeEntryCollectionNode getCommonNodeInPreviousLevels(ViewData data, TreeEntry parent) {
        TreeEntryCollectionNode parentNode = entryNodes.get(parent);
        for (TreeEntryCollectionNode node : entryNodes.values()) {
            if (node.getEntry().getViewData().equals(data)) {
                if (node.getColumIdx() <= parentNode.getColumIdx()) {
                    return node;
                }
            }
        }
        return null;
    }

    public void addRootEntry(TreeEntry rootEntry) {
        TreeEntryCollectionNode rootNode = new TreeEntryCollectionNode(rootEntry);
        entryNodes.put(rootEntry, rootNode);
    }

    // The entry exists already in a higher column. Therefore a connection is added.
    public void addConnection(TreeEntryCollectionNode to, TreeEntryCollectionNode from, int nameIndex) {
        ConnectionStart start = new ConnectionStart(from, nameIndex);
        ConnectionEnd end = new ConnectionEnd(to);
        to.addDirectConnection(start, end);
        from.addReverseDirectConnection(start, end);
        from.getEntry().setRectangleColorId(to.getEntry().getColorId(), nameIndex);
    }

    // The entry exists already in the same or a lower column. Therefore a portal is added.
    public void addPortal(TreeEntryCollectionNode to, TreeEntryCollectionNode from, int nameIndex) {
        ConnectionStart start = new ConnectionStart(from, nameIndex);
        ConnectionEnd end = new ConnectionEnd(to);
        to.addPortalConnection(start, end);
        from.addReversePortalConnection(start, end);
        from.getEntry().setRectangleColorId(to.getEntry().getColorId(), nameIndex);
    }

    // The entry does not exist. Therefore a new entry is created.
    public void addEntry(TreeEntry newEntry, TreeEntry parent, int nameIndex) {
        TreeEntryCollectionNode parentNode = entryNodes.get(parent);

        // Embed new node in tree
        TreeEntryCollectionNode newNode = new TreeEntryCollectionNode(newEntry, parentNode, nameIndex);
        entryNodes.put(newEntry, newNode);

        // set the color for the newly added entry and the connection to it
        newEntry.setColorId(colorIndex);
        parent.setRectangleColorId(colorIndex, nameIndex);
        ++colorIndex;
    }

    private void setDirty() {
        for (TreeEntryCollectionNode node : entryNodes.values()) {
            node.setDirty();
        }
    }

    public void setPortalNumbers() {
        int id = 0;
        for (TreeEntryCollectionNode node : entryNodes.values()) {
            if (!node.getPortalConnections().isEmpty()) {
                node.getEntry().enablePortal(id++);
            } else {
                node.getEntry().disablePortal();
            }
        }
    }

    public void sortTreeSetList() {
        setDirty();
        entries.clear();
        for (TreeEntryCollectionNode node : entryNodes.values()) {
            int columnId = node.getColumIdx();
            addColumn(columnId);
            entries.get(columnId).add(node.getEntry());
        }
    }

    // remove an entry if the entry is closed with the close button
    public boolean removeEntry(TreeEntry entry) {
        TreeEntryCollectionNode node = entryNodes.get(entry);
        if (node != null) {
            removeNode(node);
            return true;
        }
        return false;
    }

    private void removeNode(TreeEntryCollectionNode node) {
        List<Connection> connections = new ArrayList<>(node.getConnections());
        for (Connection connection : connections) {
            removeConnection(connection);
        }
        node.getEntry().dispose();
        entryNodes.remove(node.getEntry());
    }

    // used to remove the connection if the rectangle is clicked
    public boolean removeConnection(TreeEntry treeEntry, int nameIndex) {
        TreeEntryCollectionNode node = entryNodes.get(treeEntry);
        Optional<Connection> connection = node.getConnections().stream().filter(p -> p.getStart().getEntry().equals(treeEntry) && p.getStart()
                .getNameIndex() == nameIndex).findFirst();
        if (connection.isPresent()) {
            removeConnection(connection.get());
            return true;
        }
        return false;
    }

    // used to remove the connection if a connection is cut
    public void removeConnection(Connection connection) {
        TreeEntryCollectionNode startNode = connection.getStart().getTreeEntryNode();
        TreeEntryCollectionNode endNode = connection.getEnd().getTreeEntryNode();
        int nameIndex = connection.getStart().getNameIndex();

        endNode.removeConnection(connection);
        startNode.removeConnection(connection);
        startNode.getEntry().setRectangleState(nameIndex, TemplatorRectangleState.IDLE);

        if (!endNode.hasConnections() && !endNode.isRoot()) {
            removeNode(endNode);
        }
    }

    public void clear() {
        if (entries.size() > 0 && entries.get(0).size() > 0) {
            removeEntry(entries.get(0).first());
        }
    }

    private void addColumn(int columnIdx) {
        while (entries.size() <= columnIdx) {
            entries.add(new TreeSet<>(new TreeEntryComp()));
        }
    }

    public List<TreeSet<TreeEntry>> getEntries() {
        return entries;
    }

    public List<TreeEntry> getAllSubEntries(TreeEntry treeEntry) {
        TreeEntryCollectionNode node = entryNodes.get(treeEntry);
        List<TreeEntry> subEntries = new ArrayList<>();
        for (TreeEntryCollectionNode childNode : node.getChildren()) {
            subEntries.add(childNode.getEntry());
        }
        return subEntries;
    }

    private class TreeEntryComp implements Comparator<TreeEntry> {

        // e1 < e2 negative
        // e1 == e2 0
        // e1 > e2 positive
        @Override
        public int compare(TreeEntry e1, TreeEntry e2) {
            if (e1 == e2) {
                return 0;
            }
            TreeEntryCollectionNode node1 = entryNodes.get(e1);
            TreeEntryCollectionNode node2 = entryNodes.get(e2);
            return compare(node1, node2);
        }

        private int compare(TreeEntryCollectionNode node1, TreeEntryCollectionNode node2) {
            int y1 = Integer.MAX_VALUE;
            int y2 = Integer.MAX_VALUE;
            ConnectionStart start1 = null;
            ConnectionStart start2 = null;
            for (Connection connection : node1.getDirectConnections()) {
                int temp = connection.getStart().getEntry().getLocation().y;
                if (temp < y1) {
                    y1 = temp;
                    start1 = connection.getStart();
                }
            }
            for (Connection connection : node2.getDirectConnections()) {
                int temp = connection.getStart().getEntry().getLocation().y;
                if (temp < y2) {
                    y2 = temp;
                    start2 = connection.getStart();
                }
            }

            if (y1 == y2) {
                return start1.getNameIndex() - start2.getNameIndex();
            }
            return y1 - y2;
        }
    }

    public TreeEntryCollectionNode getNode(TreeEntry treeEntry) {
        return entryNodes.get(treeEntry);
    }

    @Override
    public Collection<INode> getNodes() {
        return new ArrayList<>(entryNodes.values());
    }
}
