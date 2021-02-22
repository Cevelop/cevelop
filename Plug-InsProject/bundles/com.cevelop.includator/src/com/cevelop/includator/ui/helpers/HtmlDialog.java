package com.cevelop.includator.ui.helpers;

import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.includator.IncludatorPlugin;


public class HtmlDialog extends MessageDialog {

    private String relativeImgPath;

    private HtmlDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType,
                       String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    private HtmlDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType,
                       String[] dialogButtonLabels, int defaultIndex, String relativeImgPath) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
        this.relativeImgPath = relativeImgPath;
    }

    private Image loadCustomImage() {
        if (relativeImgPath == null) {
            return getImage(); // fallback to default image
        }
        URL url = IncludatorPlugin.getDefault().getBundle().getResource(relativeImgPath);
        ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
        return imageDesc.createImage();
    }

    public static boolean open(Shell parent, String title, String message) {
        HtmlDialog dialog = new HtmlDialog(parent, title, null, message, INFORMATION, new String[] { "Ok" }, 0);
        int style = SWT.SHEET;
        dialog.setShellStyle(dialog.getShellStyle() | style);
        return dialog.open() == 0;
    }

    public static boolean open(Shell parent, String title, String message, String relativeImgPath) {
        HtmlDialog dialog = new HtmlDialog(parent, title, null, message, INFORMATION, new String[] { "Ok" }, 0, relativeImgPath);
        int style = SWT.SHEET;
        dialog.setShellStyle(dialog.getShellStyle() | style);
        return dialog.open() == 0;
    }

    @Override
    protected Control createMessageArea(Composite composite) {

        // create composite
        // create image
        Image image = loadCustomImage();
        if (image != null) {
            imageLabel = new Label(composite, SWT.NULL);
            image.setBackground(imageLabel.getBackground());
            imageLabel.setImage(image);
            GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(imageLabel);
        }
        // create message
        if (message != null) {
            Link link = new Link(composite, SWT.NONE);
            link.setText(message);
            link.addListener(SWT.Selection, event -> Program.launch(event.text));
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).hint(convertHorizontalDLUsToPixels(
                    IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT).applyTo(link);
        }
        return composite;
    }
}
