package com.cevelop.templator.tests;

import com.cevelop.templator.plugin.view.components.Connection;
import com.cevelop.templator.plugin.view.tree.TreeEntryCollectionNode;


public class TreeViewTestInfo {

    private int columnId;
    private int numberOfDirectConnections;
    private int numberOfReverseDirectConnections;
    private int numberOfPortalConnections;
    private int numberOfReversePortalConnections;

    public TreeViewTestInfo(int columnId, int numberOfDirectConnections, int numberOfReverseDirectConnection, int numberOfPortalConnections,
                            int numberOfReversePortalConnections) {
        this.columnId = columnId;
        this.numberOfDirectConnections = numberOfDirectConnections;
        this.numberOfReverseDirectConnections = numberOfReverseDirectConnection;
        this.numberOfPortalConnections = numberOfPortalConnections;
        this.numberOfReversePortalConnections = numberOfReversePortalConnections;
    }

    public TreeViewTestInfo(TreeEntryCollectionNode node) {
        this.columnId = node.getColumIdx();
        for (Connection connection : node.getConnections()) {
            if (connection.isPortal()) {
                if (connection.isReversed()) {
                    ++numberOfReversePortalConnections;
                } else {
                    ++numberOfPortalConnections;
                }
            } else if (connection.isReversed()) {
                ++numberOfReverseDirectConnections;
            } else {
                ++numberOfDirectConnections;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        TreeViewTestInfo other = (TreeViewTestInfo) obj;
        return (columnId == other.columnId && numberOfDirectConnections == other.numberOfDirectConnections &&
                numberOfReverseDirectConnections == other.numberOfReverseDirectConnections &&
                numberOfPortalConnections == other.numberOfPortalConnections &&
                numberOfReversePortalConnections == other.numberOfReversePortalConnections);
    }

    /** For debug purposes only. */
    @Override
    public String toString() {
        return columnId + ", " + numberOfDirectConnections + ", " + numberOfReverseDirectConnections + ", " + numberOfPortalConnections + ", " +
               numberOfReversePortalConnections;
    }
}
