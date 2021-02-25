package com.cevelop.gslator.utils;

import org.eclipse.cdt.core.dom.ast.IASTName;


public class OwnerInformation {

    private IASTName name;
    private boolean  isOwningRef;
    private boolean  isOwnerOfArray;

    public OwnerInformation(final IASTName name) {
        this.name = name;
    }

    public IASTName getName() {
        return name;
    }

    public void setName(final IASTName name) {
        this.name = name;
    }

    public boolean isOwningRef() {
        return isOwningRef;
    }

    public void setOwningRef(final boolean isOwningRef) {
        this.isOwningRef = isOwningRef;
    }

    public boolean isOwnerOfArray() {
        return isOwnerOfArray;
    }

    public void setOwnerOfArray(final boolean isOwnerOfArray) {
        this.isOwnerOfArray = isOwnerOfArray;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.toString().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return ((OwnerInformation) obj).getName().toString().equals(name.toString());
    }

}
