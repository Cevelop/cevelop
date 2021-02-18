package com.cevelop.ctylechecker.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.cevelop.ctylechecker.domain.IConfiguration;
import com.cevelop.ctylechecker.domain.ICtyleElement;
import com.cevelop.ctylechecker.service.types.CtylecheckerRuntime;
import com.cevelop.ctylechecker.ui.component.RuleComposite;
import com.cevelop.ctylechecker.ui.util.DialogResult;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


public class RuleDialog extends Dialog {

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
     */
    public RuleDialog(Shell parent, String title, IConfiguration pConfig, ICtyleElement pElement) {
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
     * @param pGrouping
     * selected group in tree or null if root
     * @param pConfig
     */
    private void createContents(IConfiguration pConfig, ICtyleElement pElement) {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.PRIMARY_MODAL);
        Image ruleImage = CtylecheckerRuntime.imageDescriptorFromPlugin("/resources/rule_img.jpg").createImage();
        shell.setImage(ruleImage);
        shell.setMinimumSize(new Point(610, 760));
        shell.setLocation(new Point(500, 100));
        shell.setSize(623, 777);
        shell.setText(getText());
        shell.setLayout(new GridLayout(1, false));

        RuleComposite rootComposite = new RuleComposite(shell, pConfig, pElement, dialogResult);
        GridData gd_rootComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_rootComposite.minimumWidth = 500;
        rootComposite.setLayoutData(gd_rootComposite);
        rootComposite.setLayout(new GridLayout(1, false));
    }
}
