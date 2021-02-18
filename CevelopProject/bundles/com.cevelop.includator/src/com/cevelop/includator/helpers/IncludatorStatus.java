/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cevelop.includator.IncludatorPlugin;


public class IncludatorStatus extends Status implements Comparable<IncludatorStatus> {

    public static final int STATUS_CODE_CUSTOM         = 0;
    public static final int STATUS_CODE_GROUPS_BASE    = 1;
    public static final int STATUS_CODE_SEVERITY_GROUP = 2;
    public static final int STATUS_CODE_FILE_GROUP     = 3;

    public IncludatorStatus(String message) {
        super(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, message);
    }

    public IncludatorStatus(String message, Exception e) {
        super(IStatus.ERROR, IncludatorPlugin.PLUGIN_ID, message, e);
    }

    public IncludatorStatus(int severity, String message, Exception e) {
        super(severity, IncludatorPlugin.PLUGIN_ID, message, e);
    }

    public IncludatorStatus(int severity, String message) {
        super(severity, IncludatorPlugin.PLUGIN_ID, message);
    }

    public IncludatorStatus(Exception e) {
        this(e.getMessage(), e);
    }

    public IncludatorStatus(int severity, Exception e) {
        this(severity, e.getMessage(), e);
    }

    @Override
    public int compareTo(IncludatorStatus other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IncludatorStatus) {
            return compareTo((IncludatorStatus) other) == 0;
        }
        return false;
    }

}
