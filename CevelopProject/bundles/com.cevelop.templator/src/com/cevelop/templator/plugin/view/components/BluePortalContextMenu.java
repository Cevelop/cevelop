package com.cevelop.templator.plugin.view.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.interfaces.IContextAction;
import com.cevelop.templator.plugin.view.interfaces.IPortalMenuActionHandler;
import com.cevelop.templator.plugin.view.interfaces.IPortalMenuActionHandler.PortalAction;


public class BluePortalContextMenu extends AbstractContextMenu {

    private List<PortalConnection>   portalConnections = new ArrayList<>();
    private IPortalMenuActionHandler callback;

    public BluePortalContextMenu(Composite parent, Set<PortalConnection> portalConnections, IPortalMenuActionHandler callback) {
        super(parent);
        this.portalConnections.addAll(portalConnections);
        this.callback = callback;
        for (int index = 0; index < portalConnections.size(); index++) {
            addMenuItem(index, PortalAction.TRAVEL_TO);
        }
    }

    @Override
    public String getText(int index, IContextAction action) {
        return super.getText(index, action) + " " + portalConnections.get(index).getPortalNumber();
    }

    @Override
    protected void performLinkAction(int index, IContextAction action) {
        callback.contextActionPerformed(portalConnections.get(index).getEnd().getEntry(), (PortalAction) action);
    }
}
