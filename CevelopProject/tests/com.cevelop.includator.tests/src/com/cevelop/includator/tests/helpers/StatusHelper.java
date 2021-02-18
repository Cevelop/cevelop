package com.cevelop.includator.tests.helpers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.ui.actions.IncludatorAction;


public class StatusHelper {

    /**
     * This will "flatten" the status hierarchy to make testing easier.
     * 
     * @return A {@link MultiStatus}
     */
    public static MultiStatus collectStatus() {
        MultiStatus collectorStatus = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_GROUPS_BASE,
                IncludatorAction.INCLUDATOR_STATUS_NAME, null);
        IncludatorPlugin.collectStatus(collectorStatus);
        MultiStatus result = unwrapStatus(collectorStatus);
        return result;
    }

    public static MultiStatus unwrapStatus(IStatus groupedStati) {
        MultiStatus result = new MultiStatus(IncludatorPlugin.PLUGIN_ID, IncludatorStatus.STATUS_CODE_CUSTOM, IncludatorAction.INCLUDATOR_STATUS_NAME,
                null);
        unwrapChildren(result, groupedStati);
        return result;
    }

    private static void unwrapChildren(MultiStatus result, IStatus statusToUnwrap) {
        int code = statusToUnwrap.getCode();
        if (code != IncludatorStatus.STATUS_CODE_GROUPS_BASE && code != IncludatorStatus.STATUS_CODE_SEVERITY_GROUP &&
            code != IncludatorStatus.STATUS_CODE_FILE_GROUP) {
            result.add(statusToUnwrap);
        }
        for (IStatus curChild : statusToUnwrap.getChildren()) {
            unwrapChildren(result, curChild);
        }
    }
}
