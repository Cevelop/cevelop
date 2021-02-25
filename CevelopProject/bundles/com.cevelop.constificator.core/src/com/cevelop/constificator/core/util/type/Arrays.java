package com.cevelop.constificator.core.util.type;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;


public class Arrays {

    public static boolean isAnyOf(ICPPClassType cls, ICPPBase[] bases) {
        for (ICPPBase base : bases) {
            if (base.getBaseClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isAnyOf(T element, T elements[]) {
        for (T current : elements) {
            if (current.equals(element)) {
                return true;
            }
        }
        return false;
    }

}
