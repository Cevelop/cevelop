package com.cevelop.includator.ui.components;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.cevelop.includator.helpers.FileHelper;


public class IncludatorProblemTree extends TreeViewer {

    final static int TEXT_MARGIN = 3;

    public IncludatorProblemTree(Composite parent, MultiStatus status) {
        super(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        setAutoExpandLevel(4);
        setContentProvider(new IncludatorProblemsContentProvider());
        ColumnViewerToolTipSupport.enableFor(this, ToolTip.NO_RECREATE);

        setLabelProvider(new WarningLableProvider());
        setInput(status);
        initContextMenu();
    }

    private void initContextMenu() {
        MenuManager menuMgr = new MenuManager();
        Menu menu = menuMgr.createContextMenu(getControl());
        menuMgr.addMenuListener(manager -> {
            if (getSelection().isEmpty()) {
                return;
            }

            if (getSelection() instanceof IStructuredSelection) {
                IStructuredSelection selection = (IStructuredSelection) getSelection();
                StatusTreeItem firstElement = (StatusTreeItem) selection.getFirstElement();
                CopyTextAction copyAction = new CopyTextAction(firstElement.getStatus());
                copyAction.setText("Copy");
                manager.add(copyAction);
                CopyTextAction copyAllAction = new CopyTextAction((IStatus) getInput());
                copyAllAction.setText("Copy all");
                manager.add(copyAllAction);
            }
        });

        menuMgr.setRemoveAllWhenShown(true);
        getControl().setMenu(menu);
    }

    private class CopyTextAction extends Action {

        private final IStatus status;

        public CopyTextAction(IStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            appendStatusString("", status, sb);
            Clipboard clipboard = new Clipboard(IncludatorProblemTree.this.getTree().getDisplay());
            clipboard.setContents(new String[] { sb.substring(FileHelper.NL.length()) }, new Transfer[] { TextTransfer.getInstance() });
            clipboard.dispose();
        }

        private void appendStatusString(String indent, IStatus status, StringBuilder sb) {
            sb.append(FileHelper.NL).append(indent).append(status.getMessage());
            if (status.getChildren().length != 0) {
                appendChildren(indent, status.getChildren(), sb);
            }
        }

        private void appendChildren(String indent, IStatus[] children, StringBuilder sb) {
            String newIndent = indent + "  ";
            for (IStatus curChild : children) {
                appendStatusString(newIndent, curChild, sb);
            }
        }
    }
}



class WarningLableProvider extends ColumnLabelProvider {

    @Override
    public String getText(Object element) {
        String text = element.toString();
        if (text.length() <= 260) { // windows crops lines longer than 260chars
            return text;
        }
        return shortenText(text);
    }

    private String shortenText(String text) {
        int cropPoint = Integer.MAX_VALUE;
        do {
            int dotPosition = text.lastIndexOf('.', cropPoint - 1);
            if (dotPosition == -1) {
                break;
            }
            cropPoint = dotPosition;
        } while (cropPoint > 250);
        if (cropPoint < text.length()) {
            return text.substring(0, cropPoint + 1) + " See tooltip for full explanation.";
        }
        return text;
    }

    @Override
    public String getToolTipText(Object element) {
        return element.toString();
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(5, 5);
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return 0;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return 5000;
    }
}
