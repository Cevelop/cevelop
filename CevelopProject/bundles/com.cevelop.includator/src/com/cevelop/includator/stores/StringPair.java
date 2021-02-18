/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

public class StringPair implements Comparable<StringPair> {

    public String second;
    public String first;

    public StringPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(StringPair other) {
        int result = first.compareTo(other.first);
        if (result != 0) {
            return result;
        }
        return second.compareTo(other.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 59 + second.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StringPair) {
            StringPair otherPair = (StringPair) other;
            return (first.equals(otherPair.first)) && (second.equals(otherPair.second));
        }
        return false;
    }

    @Override
    public String toString() {
        return first + ", " + second;
    }
}
