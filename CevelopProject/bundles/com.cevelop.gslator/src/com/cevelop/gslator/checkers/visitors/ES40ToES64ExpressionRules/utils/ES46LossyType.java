package com.cevelop.gslator.checkers.visitors.ES40ToES64ExpressionRules.utils;

public enum ES46LossyType {
    UNKNOWN(""), FpToInt("Floating Point to Integer"), FpToIntFunc("Floating Point to Integer Function Argument"), IntToCharBig(
            "Integer (>= long) to Char"), IntToCharBigFunc("Integer (>= long) to Char Function Argument"), IntToCharSmll(
                    "Integer (< long) to Char"), IntToCharSmllFunc("Integer (< long) to Char Function Argument"), ToUnsigned(
                            "signed to unsigned"), ToUnsignedFunc("signed to unsigned Function Argument"), Integer(
                                    "narrowing Integer/Char"), IntegerFunc("narrowing Integer/Char Function Argument"), Fp(
                                            "lossy Floating Point"), FpFunc("lossy Floating Point Function Argument");

    private final String msg;

    private ES46LossyType(final String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public static ES46LossyType lossyType(String type1, String type2, boolean functionArgumentVariant) {
        String casttype = normalizeType(type1) + "->" + normalizeType(type2);
        switch (casttype) {
        /* loss of floating point */
        /* floating point -> char (A) */
        case "long double->char32_t":
        case "double->char32_t":
        case "float->char32_t":
        case "long double->char16_t":
        case "double->char16_t":
        case "float->char16_t":
        case "long double->char":
        case "double->char":
        case "float->char":
            /* floating point -> integer (B) */
        case "long double->long long":
        case "long double->unsigned long long":
        case "long double->long":
        case "long double->unsigned long":
        case "long double->int":
        case "long double->unsigned":
        case "long double->short":
        case "long double->unsigned short":
        case "double->long long":
        case "double->unsigned long long":
        case "double->long":
        case "double->unsigned long":
        case "double->int":
        case "double->unsigned":
        case "double->short":
        case "double->unsigned short":
        case "float->long long":
        case "float->unsigned long long":
        case "float->long":
        case "float->unsigned long":
        case "float->int":
        case "float->unsigned":
        case "float->short":
        case "float->unsigned short":
            return functionArgumentVariant ? FpToIntFunc : FpToInt;
        /* conversion from integer to char big (C) */
        case "long long->char32_t":
        case "unsigned long long->char32_t":
        case "long->char32_t":
        case "unsigned long->char32_t":
        case "long long->char16_t":
        case "unsigned long long->char16_t":
        case "long->char16_t":
        case "unsigned long->char16_t":
        case "long long->char":
        case "unsigned long long->char":
        case "long->char":
        case "unsigned long->char":
            return functionArgumentVariant ? IntToCharBigFunc : IntToCharBig;
        /* conversion from integer to char small */
        case "int->char32_t":
        case "unsigned->char32_t":
        case "short->char32_t":
        case "unsigned short->char32_t":
        case "int->char16_t":
        case "unsigned->char16_t":
        case "short->char16_t":
        case "unsigned short->char16_t":
        case "int->char":
        case "unsigned->char":
        case "short->char":
        case "unsigned short->char":
            return functionArgumentVariant ? IntToCharSmllFunc : IntToCharSmll;
        /* loss of floating point precision */
        case "long double->double":
        case "long double->float":
        case "double->float":
            return functionArgumentVariant ? FpFunc : Fp;

        /* narrowing char conversions */
        case "char32_t->char16_t":
        case "char32_t->char":
        case "char16_t->char":
            /* narrowing int conversions */
            /* unsigned long long->smaller */
        case "unsigned long long->long long":
        case "unsigned long long->unsigned long":
        case "unsigned long long->long":
        case "unsigned long long->unsigned":
        case "unsigned long long->int":
        case "unsigned long long->unsigned short":
        case "unsigned long long->short":
            /* long long->smaller */
        case "long long->long":
        case "long long->int":
        case "long long->short":
        case "long long->unsigned long long":
        case "long long->unsigned long":
        case "long long->unsigned":
        case "long long->unsigned short":
            /* unsigned long->smaller */
        case "unsigned long->long":
        case "unsigned long->unsigned":
        case "unsigned long->int":
        case "unsigned long->unsigned short":
        case "unsigned long->short":
            /* long->smaller */
        case "long->int":
        case "long->short":
        case "long->unsigned long":
        case "long->unsigned":
        case "long->unsigned short":
            /* unsigned->smaller */
        case "unsigned->int":
        case "unsigned->unsigned short":
        case "unsigned->short":
            /* int(&smaller)->smaller (int->unsigned short below) */
        case "int->short":
        case "int->unsigned":
        case "int->unsigned short":
        case "unsigned short->short":
        case "short->unsigned short":
            return functionArgumentVariant ? IntegerFunc : Integer;
        }
        return UNKNOWN;
    }

    public static boolean isSignedToUnsigned(String type1, String type2) {
        String casttype = normalizeType(type1) + "->" + normalizeType(type2);
        switch (casttype) {
        case "long long->unsigned long long":
        case "long long->unsigned long":
        case "long long->unsigned":
        case "long long->unsigned short":
        case "long->unsigned long":
        case "long->unsigned":
        case "long->unsigned short":
        case "int->unsigned":
        case "int->unsigned short":
        case "short->unsigned short":
        case "long->unsigned long long":
        case "int->unsigned long long":
        case "int->unsigned long":
        case "short->unsigned long long":
        case "short->unsigned long":
        case "short->unsigned":
            return true;
        }
        return false;
    }

    public static String normalizeType(String type) {
        switch (type) {
        /* short int */
        case "short":
        case "short int":
        case "signed short":
        case "signed short int":
            return "short";
        case "unsigned short":
        case "unsigned short int":
            return "unsigned short";
        /* int */
        case "int":
        case "signed":
        case "signed int":
            return "int";
        case "unsigned":
        case "unsigned int":
            return "unsigned";
        /* long */
        case "long":
        case "long int":
        case "signed long":
        case "signed long int":
            return "long";
        case "unsigned long":
        case "unsigned long int":
            return "unsigned long";
        /* long long */
        case "long long":
        case "long long int":
        case "signed long long":
        case "signed long long int":
            return "long long";
        case "unsigned long long":
        case "unsigned long long int":
            return "unsigned long long";
        /* float etc? */
        default:
            return type;
        }
    }
}
