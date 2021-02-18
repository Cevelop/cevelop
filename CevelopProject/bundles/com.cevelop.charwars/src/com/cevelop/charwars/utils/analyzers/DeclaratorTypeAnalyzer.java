package com.cevelop.charwars.utils.analyzers;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;


public class DeclaratorTypeAnalyzer {

    public static boolean hasVoidType(IASTDeclarator declarator) {
        return hasType(declarator, IASTSimpleDeclSpecifier.t_void);
    }

    public static boolean hasBoolType(IASTDeclarator declarator) {
        return hasType(declarator, IASTSimpleDeclSpecifier.t_bool);
    }

    private static boolean hasType(IASTDeclarator declarator, int type) {
        IASTSimpleDeclSpecifier ds = getDeclSpecifier(declarator);
        if (ds == null) return false;
        return ds.getType() == type;
    }

    public static boolean hasCStringType(IASTDeclarator declarator) {
        return hasCStringType(declarator, true) || hasCStringType(declarator, false);
    }

    public static boolean hasCStringType(IASTDeclarator declarator, boolean isConst) {
        IASTSimpleDeclSpecifier ds = getDeclSpecifier(declarator);
        if (ds == null) return false;

        int type = ds.getType();
        boolean isValidType = type == IASTSimpleDeclSpecifier.t_char || type == IASTSimpleDeclSpecifier.t_wchar_t ||
                              type == IASTSimpleDeclSpecifier.t_char16_t || type == IASTSimpleDeclSpecifier.t_char32_t;
        return isValidType && isArrayXorPointer(declarator) && isConst == ds.isConst();
    }

    private static boolean isArrayXorPointer(IASTDeclarator declarator) {
        return isArray(declarator) ^ isPointer(declarator);
    }

    public static boolean isArray(IASTDeclarator declarator) {
        return declarator instanceof IASTArrayDeclarator;
    }

    public static boolean isPointer(IASTDeclarator declarator) {
        int numberOfPointers = 0;
        for (IASTPointerOperator po : declarator.getPointerOperators()) {
            if (po instanceof IASTPointer) {
                numberOfPointers++;
            }
        }
        return numberOfPointers == 1;
    }

    public static IASTSimpleDeclSpecifier getDeclSpecifier(IASTDeclarator declarator) {
        IASTDeclSpecifier declSpecifier = null;
        IASTNode parent = declarator.getParent();

        if (parent instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) parent;
            declSpecifier = simpleDeclaration.getDeclSpecifier();
        } else if (parent instanceof ICPPASTParameterDeclaration) {
            ICPPASTParameterDeclaration paramDeclaration = (ICPPASTParameterDeclaration) parent;
            declSpecifier = paramDeclaration.getDeclSpecifier();
        } else if (parent instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDefinition = (IASTFunctionDefinition) parent;
            declSpecifier = functionDefinition.getDeclSpecifier();
        }

        if (declSpecifier instanceof IASTSimpleDeclSpecifier) {
            return (IASTSimpleDeclSpecifier) declSpecifier;
        }
        return null;
    }
}
