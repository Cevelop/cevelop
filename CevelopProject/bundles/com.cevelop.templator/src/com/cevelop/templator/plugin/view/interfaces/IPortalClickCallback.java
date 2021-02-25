package com.cevelop.templator.plugin.view.interfaces;

import java.util.Set;

import com.cevelop.templator.plugin.view.components.PortalConnection;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public interface IPortalClickCallback {

    void bluePortalClicked(Set<PortalConnection> portals);

    void orangePortalClicked(TreeEntry treeEntry);
}
