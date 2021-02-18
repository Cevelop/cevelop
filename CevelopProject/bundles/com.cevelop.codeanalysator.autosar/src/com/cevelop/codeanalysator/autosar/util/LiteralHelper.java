package com.cevelop.codeanalysator.autosar.util;

import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.parser.util.CharArrayUtils;


public class LiteralHelper {

    public static boolean isLowerCaseHex(IASTLiteralExpression literal) {
        String literalString = literal.toString();
        if (literalString.length() < 3 || !isNumericLiteral(literal.getKind())) {
            return false;
        }
        if (!getPrefix(literalString).toUpperCase().equals("0X")) {
            return false;
        }

        String value = removePreAndPostFix(literal);
        return !value.equals(value.toUpperCase());
    }

    public static String removePreAndPostFix(IASTLiteralExpression literal) {
        String stringWithoutPrefix = literal.toString().substring(2);
        char[] suffix = getSuffix(literal.getKind(), literal.getValue(), CharArrayUtils.EMPTY);
        int endIndex = stringWithoutPrefix.length();
        endIndex -= suffix.length;
        return stringWithoutPrefix.substring(0, endIndex);
    }

    public static String getPrefix(String literalString) {
        return literalString.substring(0, 2);
    }

    public static boolean isNumericLiteral(int kind) {
        return kind == IASTLiteralExpression.lk_integer_constant || kind == IASTLiteralExpression.lk_float_constant;
    }

    public static String getSuffixAsString(int kind, char[] value) {
        char[] suffix = getSuffix(kind, value, CharArrayUtils.EMPTY);
        return (suffix.length > 0) ? new String(suffix) : "";
    }

    /* Begin CDT */
    public static char[] getSuffix(int kind, char[] value, char[] suffix) {
        if (value == null || value.length == 0) {
            return suffix;
        }
        int offset = 0;
        switch (kind) {
        case IASTLiteralExpression.lk_float_constant:
        case IASTLiteralExpression.lk_integer_constant:
            try {
                offset = (value[0] == '.') ? afterDecimalPoint(value, 0) : integerLiteral(value);
            } catch (ArrayIndexOutOfBoundsException e) {}
            break;
        case IASTLiteralExpression.lk_string_literal:
            offset = CharArrayUtils.lastIndexOf('"', value, CharArrayUtils.indexOf('"', value) + 1) + 1;
            break;
        case IASTLiteralExpression.lk_char_constant:
            offset = CharArrayUtils.lastIndexOf('\'', value, CharArrayUtils.indexOf('\'', value) + 1) + 1;
            break;
        }
        suffix = (offset > 0) ? CharArrayUtils.subarray(value, offset, -1) : suffix;
        return (suffix == null) ? CharArrayUtils.EMPTY : suffix;
    }

    private static int afterDecimalPoint(char[] value, int i) {
        char c = value[++i];
        while (isDigitOrSeparator(c) && i < value.length) {
            c = value[++i];
        }

        if ((c | 0x20) == 'e') {
            return exponentPart(value, i);
        }

        return i;
    }

    private static boolean isDigitOrSeparator(final char c) {
        return Character.isDigit(c) || (c == '\'');
    }

    private static int integerLiteral(char[] value) {
        int i = 0;
        char c = value[i++];

        if (c == '0' && i < value.length) {
            // Probably octal/hex/binary
            c = value[i];
            switch ((c | 0x20)) {
            case 'x':
                return probablyHex(value, i);
            case 'b':
                return probablyBinary(value, i);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                /*
                 * octal-literal:
                 * 0
                 * octal-literal octal-digit
                 */
                while (isOctalOrSeparator(c) && i < value.length) {
                    c = value[++i];
                }
                break;
            case '.':
                return afterDecimalPoint(value, i);
            }
            /*
             * If there is an 8 or 9, then we have a malformed octal
             */
            if (c == '8' || c == '9') {
                // eat remaining numbers
                c = value[i];
                while (Character.isDigit(c) && i < value.length) {
                    c = value[++i];
                }
            }

            /*
             * A floating-point constant could also have a leading zero
             */
            return handleDecimalOrExponent(value, c, i);
        } else if (Character.isDigit(c)) {
            /*
             * decimal-literal :
             * nonzero-digit (c has to be this to get into this else)
             * decimal-literal digit
             */
            c = value[i];
            while (isDigitOrSeparator(c) && i < value.length) {
                c = value[++i];
            }

            return handleDecimalOrExponent(value, c, i);
        } else {
            // Somehow we got called and there wasn't a digit
            // Shouldn't get here
            assert false;
        }

        return i;
    }

    private static int handleDecimalOrExponent(char[] value, char c, int i) {
        if (c == '.') {
            return afterDecimalPoint(value, i);
        } else if ((c | 0x20) == 'e') {
            return exponentPart(value, i);
        }
        return i;
    }

    private static int exponentPart(char[] value, int i) {
        char c = value[++i];

        // optional '+' or '-'
        if (c == '+' || c == '-') {
            c = value[++i];
        }

        while (isDigitOrSeparator(c) && i < value.length) {
            c = value[++i];
        }
        // If there were no digits following the 'e' then we have
        // D.De or .De which is a UDL on a double

        return i--;
    }

    private static int probablyBinary(char[] value, int i) {
        char c = value[++i];

        if (c == '1' || c == '0') {
            while (c == '1' || c == '0' || c == '\'' && i < value.length) {
                c = value[i++];
            }
            if (Character.isDigit(c)) {
                // UDL can't begin with digit, so this is a malformed binary
                return -1;
            } else if (c == '.') {
                // no such thing as binary floating point
                c = value[++i];
                while (isDigitOrSeparator(c) && i < value.length) {
                    c = value[i++];
                }
            }
        } else {
            // Here we have 0b or 0B
            return i - 1;
        }
        return i;
    }

    private static int probablyHex(char[] value, int i) {
        /*
         * hexadecimal-literal
         * 0x hexadecimal-digit
         * 0X hexadecimal-digit
         * hexadecimal-literal hexadecimal-digit
         */
        char c = value[++i];
        if (isHexDigitOrSeparator(c)) {
            while (isHexDigitOrSeparator(c) && i < value.length) {
                c = value[++i];
            }
            if (c == '.') {
                // Could be GCC's hex float
                return hexFloatAfterDecimal(value, i);
            } else if ((c | 0x20) == 'p') {
                return hexFloatExponent(value, i);
            }
        } else {
            return i - 1;
        }

        return i;
    }

    private static boolean isHexDigitOrSeparator(char c) {
        char lc = Character.toLowerCase(c);
        return (lc <= 'f' && lc >= 'a') || (c <= '9' && c >= '0') || (c == '\'');
    }

    private static boolean isOctalOrSeparator(final char c) {
        return (c >= '0' && c <= '7') || (c == '\'');
    }

    private static int hexFloatAfterDecimal(char[] value, int i) {
        // 0xHHH.
        char c = value[++i];
        if (isHexDigitOrSeparator(c)) {
            while (isHexDigitOrSeparator(c) && i < value.length) {
                c = value[++i];
            }

            if ((c | 0x20) == 'p') {
                return hexFloatExponent(value, i);
            } else {
                // The parser is very confused at this point
                // as the expression is 0x1.f
                return -1;
            }
        }

        // Probably shouldn't be able to get here
        // we have 0xHHH.
        return -1;
    }

    private static int hexFloatExponent(char[] value, int i) {
        // 0xHH.HH[pP][-+]?DDDD
        char c = value[++i];

        if (c == '-' || c == '+') {
            c = value[++i];
        }

        if (Character.isDigit(c)) {
            while (isDigitOrSeparator(c) && i < value.length) {
                c = value[++i];
            }
        } else {
            return i - 1;
        }
        return i;
    }
    /* End CDT */
}
