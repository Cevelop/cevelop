package com.cevelop.charwars.constants;

public final class QuickFixLabels {

    private QuickFixLabels() {}

    public final static String ARRAY              = "Refactor C-Array into std::array";
    public final static String C_STRING           = "Refactor C-String into std::string";
    public final static String C_STRING_ALIAS     = "Refactor C-String alias into std::string::size_type";
    public final static String C_STRING_CLEANUP   = "Refactor <cstring> function into std::string member function";
    public final static String C_STR              = "Use the function overload: ";
    public final static String POINTER_PARAMETER  = "Refactor pointer parameter into reference parameter";
    public final static String C_STRING_PARAMETER = "Refactor C-String parameter into std::string parameter";
}
