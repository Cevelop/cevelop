package com.cevelop.includator.helpers;

import java.util.Arrays;
import java.util.LinkedHashMap;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.resources.IncludatorFile;


public class ConstructorHack {

    private final LinkedHashMap<IBinding, DeclarationReference> refs;
    private final IncludatorFile                                file;

    public ConstructorHack(LinkedHashMap<IBinding, DeclarationReference> refs, IncludatorFile file) {
        this.refs = refs;
        this.file = file;
    }

    public void process(IASTSimpleDeclaration simpleDeclaration) {
        IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
        if (declSpecifier instanceof ICPPASTNamedTypeSpecifier) {
            ICPPASTNamedTypeSpecifier namedTypeSpecifier = (ICPPASTNamedTypeSpecifier) declSpecifier;
            IBinding typeBinding = BindingHelper.unwrapProblemBinding(namedTypeSpecifier.getName().resolveBinding());
            Arrays.stream(simpleDeclaration.getDeclarators()).filter(NodeHelper::hasNoPointerOperators).forEach(declarator -> {
                processCandidate(typeBinding, declarator);
            });
        }
    }

    private void processCandidate(IBinding typeBinding, IASTDeclarator declarator) {
        if (typeBinding instanceof ICPPClassType) {
            ICPPClassType classType = (ICPPClassType) typeBinding;
            ICPPConstructor constructorBinding = ConstructorReferenceHelper.getConstructorBinding(classType, file.getProject().getIndex(),
                    declarator);
            if (constructorBinding != null) {
                IASTName name = declarator.getName();
                if (refs.containsKey(constructorBinding)) {
                    DeclarationReference declRef = refs.get(constructorBinding);
                    declRef.combineMetaInfoOfNameWith(name);
                } else {
                    refs.put(constructorBinding, new ConstructorDeclarationReference(constructorBinding, name, file));
                }
            }
        } else if (typeBinding instanceof ITypedef) {
            IType aliasedType = ((ITypedef) typeBinding).getType();
            if (aliasedType instanceof IBinding) {
                processCandidate((IBinding) aliasedType, declarator);
            }
        } else if (typeBinding instanceof IProblemBinding) {
            IProblemBinding problemBinding = (IProblemBinding) typeBinding;
            for (IBinding candidate : problemBinding.getCandidateBindings()) {
                processCandidate(candidate, declarator);
            }
        }
    }

}
