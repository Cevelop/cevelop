package com.cevelop.templator.plugin.view.components;

import com.cevelop.templator.plugin.view.tree.TreeEntry;
import com.cevelop.templator.plugin.view.tree.TreeEntryCollectionNode;


public class ConnectionEnd {

    private TreeEntryCollectionNode treeEntryNode;

    public ConnectionEnd(TreeEntryCollectionNode treeEntryNode) {
        this.treeEntryNode = treeEntryNode;
    }

    public TreeEntry getEntry() {
        return treeEntryNode.getEntry();
    }

    public TreeEntryCollectionNode getTreeEntryNode() {
        return treeEntryNode;
    }
}
