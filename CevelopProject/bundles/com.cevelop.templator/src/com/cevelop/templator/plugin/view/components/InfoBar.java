package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.cevelop.templator.plugin.util.ImageCache;
import com.cevelop.templator.plugin.util.ImageCache.ImageID;
import com.cevelop.templator.plugin.view.listeners.OrangePortalListener;


public class InfoBar extends Composite {

    private Label typeImage;
    private Label portalImage;
    private Label portalDescription;

    public InfoBar(Composite parent, int style) {
        super(parent, style);
        RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
        rowLayout.marginLeft = 10;
        rowLayout.marginBottom = 0;
        rowLayout.marginTop = 0;
        setLayout(rowLayout);

        typeImage = new Label(this, SWT.None);
        typeImage.setImage(ImageCache.get(ImageID.TYPE_UNKNOWN));

        portalImage = new Label(this, SWT.NONE);
        portalImage.setImage(ImageCache.get(ImageID.PORTAL_OUT));

        portalDescription = new Label(this, SWT.NONE);
        portalDescription.setText("42"); // just a placeholder text. If it's empty the text isn't shown after
    }

    public void setTypeIcon(ImageID imageID) {
        typeImage.setImage(ImageCache.get(imageID));
    }

    public void setOrangePortalListener(OrangePortalListener orangePortalListener) {
        portalImage.addMouseListener(orangePortalListener);
        portalImage.addMouseTrackListener(orangePortalListener);
    }

    public void enablePortal(int id) {
        portalDescription.setText(id + "");
        setPortalVisible(true);
    }

    public void disablePortal() {
        setPortalVisible(false);
    }

    private void setPortalVisible(boolean visible) {
        portalImage.setVisible(visible);
        portalDescription.setVisible(visible);
    }
}
