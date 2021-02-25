package com.cevelop.includator.ui.components;

import java.util.Arrays;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;


public class SymbolListEditor extends ListEditor {

    private class SymbolValidator implements IInputValidator {

        private String[] disallowedSymbols;

        public SymbolValidator(String[] disallowedSymbols) {
            this.disallowedSymbols = disallowedSymbols;
        }

        @Override
        public String isValid(String newText) {
            if (newText.isEmpty()) {
                return "Symbol must not be empty";
            }

            for (String disallowedSymbol : disallowedSymbols) {
                if (newText.equals(disallowedSymbol)) {
                    return "Symbol is already excluded";
                }
            }

            if (containsWhitespace(newText)) {
                return "Symbol must not contain space characters";
            }

            return null;
        }

        private boolean containsWhitespace(String newText) {
            return newText.matches(".*\\s.*");
        }
    }

    public SymbolListEditor(String name, String labelText, Composite parent) {
        super(name, labelText, parent);
        getUpButton().setVisible(false);
        getDownButton().setVisible(false);
    }

    @Override
    protected String[] parseString(String stringList) {
        return stringList.split(", ");
    }

    @Override
    protected String getNewInputObject() {
        String msg = "Enter symbol to be excluded during include analysis:";
        InputDialog dlg = new InputDialog(getShell(), "Enter Symbol", msg, "", new SymbolValidator(getList().getItems()));
        if (dlg.open() == IDialogConstants.OK_ID) {
            return dlg.getValue();
        }
        return null;
    }

    @Override
    protected String createList(String[] items) {
        String itemList = Arrays.toString(items);
        return itemList.substring(1, itemList.length() - 1);
    }

    @Override
    public void store() {
        super.store();
        setPresentsDefaultValue(false);
    }
}
