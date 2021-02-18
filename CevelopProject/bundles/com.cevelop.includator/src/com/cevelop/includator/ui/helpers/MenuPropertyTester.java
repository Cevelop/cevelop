package com.cevelop.includator.ui.helpers;

import org.eclipse.core.expressions.PropertyTester;


public class MenuPropertyTester extends PropertyTester {

    private static final String C_PROJECT_RELATED_ITEM_IN_PROJECT_EXPLORER_SELECTED = "cProjectRelatedItemInProjectExplorerSelected";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (property.equals(C_PROJECT_RELATED_ITEM_IN_PROJECT_EXPLORER_SELECTED)) {
            return PropertyTesterHelper.isCProjectRelatedItemInProjectExplorerSelected();
        }
        return false;
    }

}
