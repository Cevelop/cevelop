package com.cevelop.includator.ui.helpers;

import org.eclipse.core.expressions.PropertyTester;


public class HandlerPropertyTester extends PropertyTester {

    private static final String C_PROJECT_RELATED_ITEM_SELECTED = "cProjectRelatedItemSelected";
    private static final String C_PROJECT_SELECTED              = "cProjectSelected";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (property.equals(C_PROJECT_RELATED_ITEM_SELECTED)) {
            return PropertyTesterHelper.isCProjectRelatedItemSelected();
        }
        if (property.equals(C_PROJECT_SELECTED)) {
            return PropertyTesterHelper.isCProjectSelected();
        }
        return false;
    }

}
