package com.cevelop.templator.plugin.view.interfaces;

import com.cevelop.templator.plugin.util.ImageCache.ImageID;


public interface IRectangleContextMenuActionHandler {

    public enum RectangleContextAction implements IContextAction {
        OPEN_CLOSE("Open/Close Link", ImageID.ARROW_RIGHT), GO_TO_SOURCE("Go to Source", ImageID.MAGNIFIER);

        private String        text;
        private final ImageID imageID;

        private RectangleContextAction(String text, ImageID imageID) {
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

    void contextActionPerformed(int rectangleIndex, RectangleContextAction action);
}
