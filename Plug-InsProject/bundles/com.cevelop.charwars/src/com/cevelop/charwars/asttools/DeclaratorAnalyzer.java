package com.cevelop.charwars.asttools;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;

import com.cevelop.charwars.constants.Function;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.utils.analyzers.DeclaratorTypeAnalyzer;
import com.cevelop.charwars.utils.analyzers.FunctionAnalyzer;
import com.cevelop.charwars.utils.analyzers.LiteralAnalyzer;
import com.cevelop.charwars.utils.analyzers.TypeAnalyzer;


public class DeclaratorAnalyzer {

    public static boolean isCString(IASTDeclarator declarator) {
        final boolean hasCStringType = DeclaratorTypeAnalyzer.hasCStringType(declarator);
        return hasCStringType && (hasStringLiteralAssignment(declarator) || hasStrdupAssignment(declarator));
    }

    public static boolean isCStringAlias(IASTDeclarator declarator) {
        final boolean hasCStringType = DeclaratorTypeAnalyzer.hasCStringType(declarator);
        return hasCStringType && (hasCStringAssignment(declarator) || hasOffsettedCStringAssignment(declarator));
    }

    private static boolean hasCStringAssignment(IASTDeclarator declarator) {
        final IASTInitializerClause initializerClause = getSingleElementInitializerClause(declarator.getInitializer());
        if (initializerClause != null) {
            final boolean isConversionToCharPointer = ASTAnalyzer.isConversionToCharPointer(initializerClause, true);
            if (isConversionToCharPointer) {
                final IASTFunctionCallExpression cstrCall = (IASTFunctionCallExpression) initializerClause;
                final IASTFieldReference fieldReference = (IASTFieldReference) cstrCall.getFunctionNameExpression();
                final IASTExpression fieldOwner = fieldReference.getFieldOwner();
                return TypeAnalyzer.isStdStringType(fieldOwner.getExpressionType());
            }
        }
        return false;
    }

    private static boolean hasOffsettedCStringAssignment(IASTDeclarator declarator) {
        final IASTInitializerClause initializerClause = getSingleElementInitializerClause(declarator.getInitializer());
        if (initializerClause instanceof IASTExpression) {
            final IASTExpression expr = (IASTExpression) initializerClause;
            return ASTAnalyzer.isOffsettedCString(expr);
        }
        return false;
    }

    private static boolean hasStringLiteralAssignment(IASTDeclarator declarator) {
        final IASTInitializerClause initializerClause = getSingleElementInitializerClause(declarator.getInitializer());
        return LiteralAnalyzer.isString(initializerClause);
    }

    public static boolean hasStrdupAssignment(IASTDeclarator declarator) {
        final IASTInitializerClause initializerClause = getSingleElementInitializerClause(declarator.getInitializer());
        return FunctionAnalyzer.isCallToFunction(initializerClause, Function.STRDUP);
    }

    public static IASTInitializerClause getInitializerClause(IASTDeclarator declarator) {
        final IASTInitializer initializer = declarator.getInitializer();
        if (initializer instanceof IASTEqualsInitializer) {
            final IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            return equalsInitializer.getInitializerClause();
        }
        return null;
    }

    public static IASTInitializerClause getSingleElementInitializerClause(IASTInitializer initializer) {
        if (initializer instanceof IASTEqualsInitializer) {
            final IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            IASTInitializerClause clause = equalsInitializer.getInitializerClause();
            if (clause instanceof IASTInitializerList) {
                final IASTInitializerList initializerList = (IASTInitializerList) clause;
                if (initializerList.getClauses().length == 1) {
                    clause = initializerList.getClauses()[0];
                }
            }
            return clause;
        } else if (initializer instanceof IASTInitializerList) {
            final IASTInitializerList initializerList = (IASTInitializerList) initializer;
            if (initializerList.getClauses().length == 1) {
                return initializerList.getClauses()[0];
            }
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            final ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) initializer;
            if (constructorInitializer.getArguments().length == 1) {
                return constructorInitializer.getArguments()[0];
            }
        }
        return null;
    }

    public static String getStringReplacementType(IASTDeclarator declarator) {
        final IASTSimpleDeclSpecifier ds = DeclaratorTypeAnalyzer.getDeclSpecifier(declarator);
        if (ds == null) {
            return null;
        }

        switch (ds.getType()) {
        case IASTSimpleDeclSpecifier.t_wchar_t:
            return StdString.STD_WSTRING;
        case IASTSimpleDeclSpecifier.t_char16_t:
            return StdString.STD_U16STRING;
        case IASTSimpleDeclSpecifier.t_char32_t:
            return StdString.STD_U32STRING;
        default:
            return StdString.STD_STRING;
        }
    }
}
