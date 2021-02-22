package com.cevelop.includator.ui.helpers;

import org.eclipse.jface.dialogs.IInputValidator;


public class SubDirPathInputDialog implements IInputValidator {

    @Override
    public String isValid(String newText) {
        if (newText.matches("([a-zA-Z]:\\\\.*)|(\\/.*)")) {
            return "The paht entered is absolute.";
        }
        return null;
    }
}
