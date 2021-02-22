package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.view.interfaces.IContextAction;
import com.cevelop.templator.plugin.view.interfaces.IRectangleContextMenuActionHandler;
import com.cevelop.templator.plugin.view.interfaces.IRectangleContextMenuActionHandler.RectangleContextAction;


public class RectangleContextMenu extends AbstractContextMenu {

    private IRectangleContextMenuActionHandler callback;

    public RectangleContextMenu(Composite parent, int rectangleIndex, IRectangleContextMenuActionHandler callback) {
        super(parent);
        this.callback = callback;
        addMenuItem(rectangleIndex, RectangleContextAction.OPEN_CLOSE);
        addMenuItem(rectangleIndex, RectangleContextAction.GO_TO_SOURCE);
    }

    @Override
    protected void performLinkAction(int index, IContextAction action) {
        callback.contextActionPerformed(index, (RectangleContextAction) action);
    }
}
