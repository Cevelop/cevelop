package com.cevelop.templator.plugin.view.interfaces;

import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.tree.TreeEntry;


public interface IPortalMenuActionHandler {

    public enum PortalAction implements IContextAction {

        TRAVEL_TO("travel to", ImageID.PORTAL_HOVER);

        private final String  text;
        private final ImageID imageID;

        private PortalAction(String text, ImageID imageID) {
            this.text = text;
            this.imageID = imageID;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public ImageID getImageID() {
            return imageID;
        }
    }

    void contextActionPerformed(TreeEntry entry, PortalAction action);
}
