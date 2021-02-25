/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers.comparators;

import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;

import com.cevelop.includator.helpers.IncludatorStatus;


public class StatusComparator implements Comparator<IStatus> {

    @Override
    public int compare(IStatus first, IStatus second) {
        if ((first instanceof IncludatorStatus) && (second instanceof IncludatorStatus)) {
            IncludatorStatus includatorStatusFirst = (IncludatorStatus) first;
            IncludatorStatus includatorStatusSecond = (IncludatorStatus) second;
            return includatorStatusFirst.compareTo(includatorStatusSecond);
        }
        return first.toString().compareTo(second.toString());
    }
}
