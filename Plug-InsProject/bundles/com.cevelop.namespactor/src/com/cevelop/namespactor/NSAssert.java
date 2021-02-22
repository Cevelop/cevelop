/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor;

/**
 * @author kunz@ideadapt.net
 */
public class NSAssert {

    public static <T> boolean isInstanceOf(Class<T> clazz, Object obj) {
        return org.eclipse.core.runtime.Assert.isTrue(clazz.isAssignableFrom(obj.getClass()), String.format(
                "instance expected to be of type %s but was %s", clazz, obj.getClass()));
    }
}
