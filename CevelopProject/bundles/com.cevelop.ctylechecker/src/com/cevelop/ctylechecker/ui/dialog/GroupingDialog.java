package com.cevelop.ctylechecker.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.component.GroupingComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;


public class GroupingDialog extends Dialog {

    private GroupingComposite rootComposite;

    private DialogResult dialogResult = new DialogResult();
    protected Shell      shell;

    /**
     * Create the dialog.
     *
     * @param parent
     * The shell
     * @param title
     * Title of this dialog
     * @param pConfig
     * The configuration
     * @param pElement
     * The style element
     *
     */
    public GroupingDialog(Shell parent, String title, IConfiguration pConfig, ICtyleElement pElement) {
        super(parent);
        setText(title);
        createContents(pConfig, pElement);
    }

    /**
     * Open the dialog.
     *
     * @return the result
     */
    public int open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return dialogResult.getResult();
    }

    /**
     * Create contents of the dialog.
     *
     * @param pConfig
     * The configuration
     * @param pElement
     * The style element
     */
    private void createContents(IConfiguration pConfig, ICtyleElement pElement) {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM);
        Image groupImage = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/group_img.jpg").createImage();
        shell.setImage(groupImage);
        shell.setLocation(new Point(500, 200));
        shell.setSize(450, 183);
        shell.setText(getText());
        shell.setLayout(new GridLayout(1, false));
        rootComposite = new GroupingComposite(shell, pConfig, pElement, dialogResult);
        rootComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }
}
