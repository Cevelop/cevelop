package com.cevelop.includator.helpers;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.resources.IncludatorFile;


public class ParameterDeclarationHack {

    private final IncludatorFile file;
    private final RefMap         refs;

    public ParameterDeclarationHack(RefMap refs, IncludatorFile file) {
        this.refs = refs;
        this.file = file;
    }

    public void process(IASTParameterDeclaration parameterDeclaration) {
        IASTDeclSpecifier declSpecifier = parameterDeclaration.getDeclSpecifier();
        if (declSpecifier instanceof ICPPASTNamedTypeSpecifier) {
            IBinding namedType = ((ICPPASTNamedTypeSpecifier) declSpecifier).getName().resolveBinding();
            Optional<IASTDeclarator> valueDeclarator = NodeHelper.getFirstValueDeclarator(parameterDeclaration.getDeclarator());
            if (namedType instanceof ICPPClassType) {
                ICPPClassType classType = (ICPPClassType) namedType;
                valueDeclarator.ifPresent(declarator -> addCopyConstructorRef(classType, declarator));
            }
        }
    }

    private void addCopyConstructorRef(ICPPClassType type, IASTDeclarator declarator) {
        ICPPConstructor[] constructors = null;
        if (type instanceof ICPPClassSpecialization) {
            constructors = type.getConstructors();
        } else {
            constructors = type.getConstructors();
        }
        for (ICPPConstructor curConstructor : constructors) {
            if (ConstructorReferenceHelper.isCopyConstructor(curConstructor, type)) {
                ConstructorDeclarationReference addedRef = ConstructorReferenceHelper.addImplicitConstrRefToRefMap(curConstructor, refs, declarator
                        .getName(), file);
                addedRef.addIsRefNameToMetaInfo();
                return;
            }
        }
    }
}
