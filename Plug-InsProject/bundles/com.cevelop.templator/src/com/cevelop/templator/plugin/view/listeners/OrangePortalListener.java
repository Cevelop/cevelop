package com.cevelop.templator.plugin.view.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.interfaces.IPortalClickCallback;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public class OrangePortalListener implements MouseListener, MouseTrackListener {

    private Cursor cursorArrow;
    private Cursor cursorPortalHover;

    private TreeEntry            treeEntry;
    private Composite            composite;
    private IPortalClickCallback clickCallback;

    public OrangePortalListener(TreeEntry treeEntry, Composite composite, IPortalClickCallback clickCallback) {
        this.treeEntry = treeEntry;
        this.composite = composite;
        this.clickCallback = clickCallback;
        cursorArrow = new Cursor(composite.getDisplay(), SWT.CURSOR_ARROW);
        ImageData idPortalHover = ImageCache.get(ImageID.PORTAL_HOVER).getImageData();
        cursorPortalHover = new Cursor(composite.getDisplay(), idPortalHover, 5, 5);
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {}

    @Override
    public void mouseDown(MouseEvent e) {
        clickCallback.orangePortalClicked(treeEntry);
    }

    @Override
    public void mouseUp(MouseEvent e) {}

    @Override
    public void mouseEnter(MouseEvent e) {
        composite.setCursor(cursorPortalHover);
    }

    @Override
    public void mouseExit(MouseEvent e) {
        composite.setCursor(cursorArrow);
    }

    @Override
    public void mouseHover(MouseEvent e) {}
}
