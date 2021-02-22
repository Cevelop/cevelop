package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.view.interfaces.IContextAction;


public abstract class AbstractContextMenu {

    public static int NO_INDEX = 0;
    private Menu      contextMenu;

    public AbstractContextMenu(Composite parent) {
        contextMenu = new Menu(parent);
        contextMenu.setVisible(true);
    }

    protected abstract void performLinkAction(final int index, final IContextAction action);

    // override if the text should not be the text of the action
    protected String getText(int index, IContextAction action) {
        return action.getText();
    }

    protected void addMenuItem(final int index, final IContextAction action) {
        MenuItem item = new MenuItem(contextMenu, SWT.NONE);
        item.setText(getText(index, action));
        item.setImage(ImageCache.get(action.getImageID()));
        item.addListener(SWT.Selection, e -> performLinkAction(index, action));
    }
}
