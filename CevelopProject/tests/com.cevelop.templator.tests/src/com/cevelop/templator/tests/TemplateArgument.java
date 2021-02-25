package com.cevelop.templator.tests;

import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBasicType.Kind;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPBasicType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPPointerType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPQualifierType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateTypeArgument;


public class TemplateArgument {

    public static final IType                INT_TYPE;
    public static final IType                INT_POINTER_TYPE;
    public static final ICPPTemplateArgument INT;
    public static final ICPPTemplateArgument INT_POINTER;
    public static final ICPPTemplateArgument INT_POINTER_CONST;
    public static final ICPPTemplateArgument INT_REFERENCE;
    public static final ICPPTemplateArgument INT_CONST_REFERENCE;
    public static final ICPPTemplateArgument DOUBLE;
    public static final ICPPTemplateArgument CHAR;
    public static final ICPPTemplateArgument LONG;
    public static final ICPPTemplateArgument UNSIGNED_LONG;
    public static final ICPPTemplateArgument UNSIGNED_LONG_LONG;
    public static final ICPPTemplateArgument BOOL;

    static {
        INT_TYPE = new CPPBasicType(Kind.eInt, 0);
        INT_POINTER_TYPE = new CPPPointerType(INT_TYPE);
        INT = new CPPTemplateTypeArgument(INT_TYPE);
        INT_POINTER = new CPPTemplateTypeArgument(INT_POINTER_TYPE);
        INT_POINTER_CONST = new CPPTemplateTypeArgument(new CPPQualifierType(INT_POINTER_TYPE, true, false));
        INT_REFERENCE = new CPPTemplateTypeArgument(new CPPReferenceType(INT_TYPE, false));
        INT_CONST_REFERENCE = new CPPTemplateTypeArgument(new CPPReferenceType(new CPPQualifierType(INT_TYPE, true, false), false));
        DOUBLE = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eDouble, 0));
        CHAR = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eChar, 0));
        LONG = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eInt, IBasicType.IS_LONG));
        UNSIGNED_LONG = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eInt, IBasicType.IS_LONG | IBasicType.IS_UNSIGNED));
        UNSIGNED_LONG_LONG = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eInt, IBasicType.IS_LONG_LONG | IBasicType.IS_UNSIGNED));
        BOOL = new CPPTemplateTypeArgument(new CPPBasicType(Kind.eBoolean, 0, null));
    }
}
