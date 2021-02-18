package com.cevelop.charwars.constants;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;


public class StringType {

    public final static StringType STRING  = new StringType(StdString.STRING, StdString.STRING_SIZE_TYPE);
    public final static StringType WSTRING = new StringType(StdString.WSTRING, StdString.WSTRING_SIZE_TYPE);

    private String sizeType;
    private String className;

    public static StringType createFromDeclSpecifier(IASTDeclSpecifier declSpecifier) {
        if (declSpecifier instanceof IASTSimpleDeclSpecifier) {
            IASTSimpleDeclSpecifier simpleDeclSpecifier = (IASTSimpleDeclSpecifier) declSpecifier;
            if (simpleDeclSpecifier.getType() == IASTSimpleDeclSpecifier.t_wchar_t) {
                return WSTRING;
            }
        }
        return STRING;
    }

    private StringType(String className, String sizeType) {
        this.className = className;
        this.sizeType = sizeType;
    }

    public String getClassName() {
        return className;
    }

    public String getSizeType() {
        return sizeType;
    }
}
