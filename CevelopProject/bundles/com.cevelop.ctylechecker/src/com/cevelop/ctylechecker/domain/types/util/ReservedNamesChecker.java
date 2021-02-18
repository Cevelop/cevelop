package com.cevelop.ctylechecker.domain.types.util;

import java.util.ArrayList;
import java.util.List;


public class ReservedNamesChecker {

    public static final List<String> reservedNames = new ArrayList<String>() {

        private static final long serialVersionUID = 5865162114554941835L;
        {
            add("main");
        }
    };

    public static Boolean check(String pName) {
        return reservedNames.contains(pName);
    }
}
