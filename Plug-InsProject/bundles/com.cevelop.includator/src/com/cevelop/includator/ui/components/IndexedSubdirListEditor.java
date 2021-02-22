package com.cevelop.includator.ui.components;

import java.util.Arrays;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

import com.cevelop.includator.ui.helpers.SubDirPathInputDialog;


public class IndexedSubdirListEditor extends ListEditor {

    public IndexedSubdirListEditor(String name, Composite parent) {
        super(name, "Subdirectories to be indexed:", parent);
    }

    @Override
    protected String[] parseString(String stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new String[0];
        }
        return stringList.split(",\\s*");
    }

    @Override
    protected String getNewInputObject() {
        String msg = "Enter the name of a directory which is a subdir of any include directory.";
        InputDialog dlg = new InputDialog(getShell(), "Enter subdir path.", msg, "", new SubDirPathInputDialog());
        if (dlg.open() == IDialogConstants.OK_ID) {
            return dlg.getValue();
        }
        return null;
    }

    @Override
    protected String createList(String[] items) {
        String strList = Arrays.toString(items);
        return strList.substring(1, strList.length() - 1);
    }
}
