package com.cevelop.charwars.constants;

import java.util.Arrays;
import java.util.List;


public final class Constants {

    private Constants() {}

    //others
    public final static String       NULLPTR     = "nullptr";
    public final static String       AUTO        = "auto";
    public final static String       STD_PREFIX  = "std::";
    public final static String       STRDUP      = "strdup";
    public final static String       COUT        = "cout";
    public final static String       STD_COUT    = "std::cout";
    public final static List<String> NULL_VALUES = Arrays.asList(new String[] { Constants.NULLPTR, "0" });
    public final static String       ASSERT      = "assert";
}
