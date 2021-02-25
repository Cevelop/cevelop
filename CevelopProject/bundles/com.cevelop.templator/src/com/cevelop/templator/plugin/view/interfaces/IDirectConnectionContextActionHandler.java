package com.cevelop.templator.plugin.view.interfaces;

import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.components.DirectConnection;


public interface IDirectConnectionContextActionHandler {

    public enum DirectConnectionContextAction implements IContextAction {

        CUT("cut", ImageID.CUT), TRAVEL_TO_START("start", ImageID.TRAVEL_TO_START), TRAVEL_TO_END("end", ImageID.TRAVEL_TO_END);

        private final String  text;
        private final ImageID imageID;

        private DirectConnectionContextAction(String text, ImageID imageID) {
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

    void directConnectionContextActionPerformed(DirectConnection directConnection, DirectConnectionContextAction action);
}
