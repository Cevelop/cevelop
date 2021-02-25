package com.cevelop.templator.plugin.view.interfaces;

import org.eclipse.swt.events.MouseMoveListener;

import com.cevelop.templator.plugin.view.components.Portal;


public interface IPortalMouseListener extends MouseMoveListener {

    void findHoveredPortals(int x, int y);

    boolean isPortalHovered(Portal portal, int x, int y);
}
