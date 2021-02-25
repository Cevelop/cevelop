package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.interfaces.IContextAction;
import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionContextActionHandler;
import com.cevelop.templator.plugin.view.interfaces.IDirectConnectionContextActionHandler.DirectConnectionContextAction;


public class DirectConnectionContextMenu extends AbstractContextMenu {

    private DirectConnection                      directConnection;
    private IDirectConnectionContextActionHandler callback;

    public DirectConnectionContextMenu(Composite parent, DirectConnection directConnection, IDirectConnectionContextActionHandler callback) {
        super(parent);
        this.directConnection = directConnection;
        this.callback = callback;
        addMenuItem(NO_INDEX, DirectConnectionContextAction.CUT);
        addMenuItem(NO_INDEX, DirectConnectionContextAction.TRAVEL_TO_START);
        addMenuItem(NO_INDEX, DirectConnectionContextAction.TRAVEL_TO_END);
    }

    @Override
    protected void performLinkAction(int index, IContextAction action) {
        callback.directConnectionContextActionPerformed(directConnection, (DirectConnectionContextAction) action);
    }
}
