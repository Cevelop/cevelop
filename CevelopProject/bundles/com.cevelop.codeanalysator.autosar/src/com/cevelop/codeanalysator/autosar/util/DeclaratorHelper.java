package com.cevelop.codeanalysator.autosar.util;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTVirtSpecifier;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


public class DeclaratorHelper {

    public static IASTDeclSpecifier findDeclSpec(IASTNode parent) {
        if (parent instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) parent;
            return funcDef.getDeclSpecifier();
        } else if (parent instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) parent;
            return simpleDecl.getDeclSpecifier();
        }
        /*
         * else-block should never be reached because parent is already type-checked in
         * isApplicable() but you never know
         */
        return null;

    }

    public static ICPPASTFunctionDeclarator createFunctionDeclarator(ICPPNodeFactory factory, IASTFunctionDeclarator funcDeclarator,
            SpecifierKind kind) {
        ICPPASTFunctionDeclarator decl = (ICPPASTFunctionDeclarator) funcDeclarator.copy();
        ICPPASTFunctionDeclarator icppastFunctionDeclarator = factory.newFunctionDeclarator(funcDeclarator.getName().copy());
        icppastFunctionDeclarator.addVirtSpecifier(new CPPASTVirtSpecifier(kind));
        icppastFunctionDeclarator.setNoexceptExpression(decl.getNoexceptExpression());
        icppastFunctionDeclarator.setConst(decl.isConst());
        for (IASTParameterDeclaration param : decl.getParameters()) {
            icppastFunctionDeclarator.addParameterDeclaration(param);
        }
        icppastFunctionDeclarator.setInitializer(decl.getInitializer());
        icppastFunctionDeclarator.setRefQualifier(decl.getRefQualifier());
        icppastFunctionDeclarator.setVolatile(decl.isVolatile());
        icppastFunctionDeclarator.setTrailingReturnType(decl.getTrailingReturnType());
        return icppastFunctionDeclarator;
    }

    public static IASTDeclSpecifier createDeclSpec(IASTDeclSpecifier declSpec, IBetterFactory factory) {
        if (declSpec instanceof ICPPASTSimpleDeclSpecifier) {
            ICPPASTSimpleDeclSpecifier simpleDeclSpec = (ICPPASTSimpleDeclSpecifier) declSpec;
            ICPPASTSimpleDeclSpecifier newSimpleDeclSpec = factory.newSimpleDeclSpecifier();
            newSimpleDeclSpec.setType(simpleDeclSpec.getType());
            return newSimpleDeclSpec;
        } else if (declSpec instanceof ICPPASTNamedTypeSpecifier) {
            ICPPASTNamedTypeSpecifier namedSpec = (ICPPASTNamedTypeSpecifier) declSpec;
            ICPPASTNamedTypeSpecifier newNamedSpec = namedSpec.copy();
            newNamedSpec.setVirtual(false);
            return newNamedSpec;
        }
        throw new IllegalArgumentException("Passed invalid decl specifier");
    }

}
