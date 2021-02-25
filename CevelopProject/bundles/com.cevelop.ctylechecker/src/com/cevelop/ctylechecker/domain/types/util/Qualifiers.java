package com.cevelop.ctylechecker.domain.types.util;

import java.util.Arrays;
import java.util.List;


public final class Qualifiers {

    public static final String CONST        = "const";
    public static final String AUTO         = "auto";
    public static final String STATIC       = "static";
    public static final String CONSTEXPR    = "constexpr";
    public static final String EXTERN       = "extern";
    public static final String EXTERN_C     = "extern 'C'";
    public static final String MUTABLE      = "mutable";
    public static final String DEFAULT      = "default";
    public static final String PRIVATE      = "private";
    public static final String PROTECTED    = "protected";
    public static final String PUBLIC       = "public";
    public static final String DELETED      = "deleted";
    public static final String NO_RETURN    = "no return";
    public static final String REGISTER     = "register";
    public static final String INLINE       = "inline";
    public static final String DESTRUCTOR   = "destructor";
    public static final String EXPLICIT     = "explicit";
    public static final String FINAL        = "final";
    public static final String OVERRIDE     = "override";
    public static final String PURE_VIRTUAL = "pure virtual";
    public static final String IMPLICIT     = "implicit";
    public static final String VIRTUAL      = "virtual";
    public static final String FILE_BODY    = "File Body";
    public static final String FILE_ENDING  = "File Ending";

    public static List<String> fileQualifiers() {
        return ListUtil.asSortedList(Arrays.asList(FILE_BODY, FILE_ENDING));
    }

    public static List<String> variableQualifiers() {
        return ListUtil.asSortedList(Arrays.asList(CONST, AUTO, STATIC, CONSTEXPR, EXTERN, EXTERN_C, MUTABLE, DEFAULT));
    }

    public static List<String> fieldQualifiers() {
        return ListUtil.asSortedList(Arrays.asList(CONST, AUTO, STATIC, CONSTEXPR, EXTERN, EXTERN_C, MUTABLE, DEFAULT, PRIVATE, PROTECTED, PUBLIC));
    }

    public static List<String> functionQualifiers() {
        return ListUtil.asSortedList(Arrays.asList(STATIC, DEFAULT, CONST, CONSTEXPR, EXTERN, EXTERN_C, MUTABLE, DELETED, NO_RETURN, REGISTER,
                INLINE));
    }

    public static List<String> methodQualifiers() {
        return ListUtil.asSortedList(Arrays.asList(STATIC, DEFAULT, CONST, CONSTEXPR, EXTERN, EXTERN_C, MUTABLE, DELETED, NO_RETURN, REGISTER, INLINE,
                DESTRUCTOR, EXPLICIT, FINAL, OVERRIDE, PURE_VIRTUAL, IMPLICIT, VIRTUAL, PRIVATE, PROTECTED, PUBLIC));
    }
}
