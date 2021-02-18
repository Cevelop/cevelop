/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeHelper {

    public static String toDurationString(long millisStart, long millisStop) {
        Date elapsedTime = new Date(millisStop - millisStart - 3600000);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        return format.format(elapsedTime);
    }
}
