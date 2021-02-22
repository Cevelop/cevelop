/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.includesubstituion;

public class WeightedObject<E> {

    public long    weight;
    public final E object;

    public WeightedObject(long weight, E object) {
        this.weight = weight;
        this.object = object;
    }

    @Override
    public String toString() {
        return object + " weight: " + weight;
    }
}
