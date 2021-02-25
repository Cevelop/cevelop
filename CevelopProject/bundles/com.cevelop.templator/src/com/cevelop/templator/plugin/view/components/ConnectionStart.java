package com.cevelop.templator.plugin.view.components;

import com.cevelop.templator.plugin.view.tree.TreeEntryCollectionNode;


public class ConnectionStart extends ConnectionEnd {

    private int nameIndex;

    public ConnectionStart(TreeEntryCollectionNode treeEntryNode, int nameIndex) {
        super(treeEntryNode);
        this.nameIndex = nameIndex;
    }

    public int getRectangleOffset() {
        return getEntry().getRectOffset(nameIndex);
    }

    public int getRectangleHeight() {
        return getEntry().getRectHeight(nameIndex);
    }

    public int getNameIndex() {
        return nameIndex;
    }
}
