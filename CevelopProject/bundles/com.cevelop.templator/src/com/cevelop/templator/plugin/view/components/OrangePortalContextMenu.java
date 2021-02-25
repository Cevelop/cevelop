package com.cevelop.templator.plugin.view.components;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.interfaces.IContextAction;
import com.cevelop.templator.plugin.view.interfaces.IPortalMenuActionHandler;
import com.cevelop.templator.plugin.view.interfaces.IPortalMenuActionHandler.PortalAction;


public class OrangePortalContextMenu extends AbstractContextMenu {

    private List<PortalConnection>   portalConnections;
    private IPortalMenuActionHandler callback;

    public OrangePortalContextMenu(Composite parent, List<PortalConnection> portalConnections, IPortalMenuActionHandler callback) {
        super(parent);
        this.portalConnections = portalConnections;
        this.callback = callback;
        for (int index = 0; index < portalConnections.size(); index++) {
            addMenuItem(index, PortalAction.TRAVEL_TO);
        }
    }

    @Override
    public String getText(int index, IContextAction action) {
        return super.getText(index, action) + " " + portalConnections.get(index).getStart().getEntry().getTitle();
    }

    @Override
    protected void performLinkAction(int index, IContextAction action) {
        callback.contextActionPerformed(portalConnections.get(index).getStart().getEntry(), (PortalAction) action);
    }
}
