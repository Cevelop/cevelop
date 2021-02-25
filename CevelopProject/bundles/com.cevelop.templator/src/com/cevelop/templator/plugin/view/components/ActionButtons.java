package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback;
import com.cevelop.templator.plugin.view.interfaces.IActionButtonCallback.ButtonAction;


public class ActionButtons extends Composite {

    private IActionButtonCallback callback;
    private ToolBar               actionButtonToolBar;

    public ActionButtons(Composite parent, int style, final IActionButtonCallback callback) {
        super(parent, style);

        this.callback = callback;

        setLayout(new GridLayout(1, false));

        actionButtonToolBar = new ToolBar(this, SWT.NONE);

        createActionButton(ImageCache.get(ImageID.ICON_CON_MIN), ButtonAction.MINIMIZE);
        createActionButton(ImageCache.get(ImageID.ICON_CON_MAX), ButtonAction.MAXIMIZE);
        createActionButton(ImageCache.get(ImageID.ICON_REMOVE), ButtonAction.CLOSE);

        final ToolBar contextMenuToolBar = new ToolBar(this, SWT.NONE);
        GridData gridData = new GridData(SWT.RIGHT, SWT.TOP, true, false);
        gridData.horizontalSpan = 3;
        contextMenuToolBar.setLayoutData(gridData);

        ToolItem menuButton = new ToolItem(contextMenuToolBar, SWT.PUSH);
        menuButton.setImage(ImageCache.get(ImageID.ICON_MENU));
        menuButton.setToolTipText("View Entry Context Menu");
        menuButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                showActionMenu(contextMenuToolBar);
            }
        });
    }

    private void showActionMenu(final Control button) {
        Menu contextMenu = new Menu(this);

        createMenuAction(contextMenu, ButtonAction.GO_TO_SOURCE);
        new MenuItem(contextMenu, SWT.SEPARATOR);
        createMenuAction(contextMenu, ButtonAction.SEARCH);
        new MenuItem(contextMenu, SWT.SEPARATOR);
        createMenuAction(contextMenu, ButtonAction.CLOSE_ALL);
        createMenuAction(contextMenu, ButtonAction.MINIMIZE_ALL);
        createMenuAction(contextMenu, ButtonAction.MAXIMIZE_ALL);
        new MenuItem(contextMenu, SWT.SEPARATOR);
        createMenuAction(contextMenu, ButtonAction.PROBLEMS);

        final Point buttonMap = button.getDisplay().map(button, null, new Point(0, button.getSize().y));
        contextMenu.setLocation(buttonMap);
        contextMenu.setVisible(true);
    }

    private void createActionButton(Image image, final ButtonAction action) {
        ToolItem button = new ToolItem(actionButtonToolBar, SWT.PUSH);
        button.setImage(image);
        button.setToolTipText(action.getText());
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                callback.actionPerformed(action);
            }
        });
    }

    private void createMenuAction(Menu contextMenu, final ButtonAction action) {
        MenuItem item = new MenuItem(contextMenu, SWT.NONE);
        item.setText(action.getText());
        item.addListener(SWT.Selection, e -> callback.actionPerformed(action));
    }
}
