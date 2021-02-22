/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.stores;

public class StringIntegerPair implements Comparable<StringIntegerPair> {

    public String string;
    public int    integer;

    public StringIntegerPair(String string, int integer) {
        this.string = string;
        this.integer = integer;
    }

    @Override
    public int compareTo(StringIntegerPair other) {
        int result = integer - other.integer;
        if (result != 0) {
            return result;
        }
        return string.compareTo(other.string);
    }

    @Override
    public int hashCode() {
        return string.hashCode() * 59 + integer;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StringIntegerPair) {
            StringIntegerPair otherPair = (StringIntegerPair) other;
            return (integer == otherPair.integer) && (string.equals(otherPair.string));
        }
        return false;
    }

    @Override
    public String toString() {
        return string + "," + integer;
    }
}
