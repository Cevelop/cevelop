package com.cevelop.intwidthfixator.quickfixes;

import com.cevelop.intwidthfixator.helpers.IdHelper.ProblemId;


public class ProblemToLabelMapper {

    public static String getLabel(ProblemId id) {
        switch (id) {
        case CASTS:
            return "Replace target type with fixed width integer type";
        case FUNCTION:
            return "Replace function parameter with fixed width integer type";
        case TEMPLATE:
            return "Replace template parameter with fixed width integer type";
        case TYPEDEF:
            return "Replace mapped type with fixed width integer type";
        case VARIABLES:
            return "Replace variable definition with fixed width integer type";
        }
        throw new IllegalStateException("Mapping of problemIds incomplete");
    }
}
