package com.cevelop.includator.helpers;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.ImplicitDestructorDeclarationReference;
import com.cevelop.includator.resources.IncludatorFile;


public class DestructorHack {

    private final LinkedHashMap<IBinding, DeclarationReference> refs;
    private final IncludatorFile                                file;

    public DestructorHack(LinkedHashMap<IBinding, DeclarationReference> refs, IncludatorFile file) {
        this.refs = refs;
        this.file = file;
    }

    public void process(IASTSimpleDeclaration declaration) {
        process(declaration, declaration.getDeclSpecifier(), declaration.getDeclarators());
    }

    public void process(IASTParameterDeclaration declaration) {
        process(declaration, declaration.getDeclSpecifier(), declaration.getDeclarator());
    }

    private void process(IASTNode declaration, IASTDeclSpecifier declSpecifier, IASTDeclarator... declarators) {
        if (shouldIgnore(declaration, declSpecifier, declarators)) {
            return;
        }
        Optional<IASTDeclarator> firstValueDeclarator = NodeHelper.getFirstValueDeclarator(declarators);
        firstValueDeclarator.ifPresent(declarator -> {
            ICPPASTNamedTypeSpecifier namedSpecifier = (ICPPASTNamedTypeSpecifier) declSpecifier;
            IBinding binding = namedSpecifier.getName().resolveBinding();
            processCandidate(binding, declarator.getName());

        });
    }

    private boolean shouldIgnore(IASTNode declaration, IASTDeclSpecifier declSpecifier, IASTDeclarator... declarators) {
      //@formatter:off
		return 	!(declSpecifier instanceof ICPPASTNamedTypeSpecifier)
				|| declSpecifier.getStorageClass() == IASTDeclSpecifier.sc_typedef
				|| declarators == null
				|| IASTCompositeTypeSpecifier.MEMBER_DECLARATION.equals(declaration.getPropertyInParent());
		//@formatter:on
    }

    private void processCandidate(IBinding binding, IASTName name) {
        if (binding instanceof ICPPClassType) {
            ICPPClassType classType = (ICPPClassType) binding;
            ICPPMethod destructorBinding = DestructorReferenceHelper.getDestructorBinding(classType, file.getProject().getIndex(), name);
            if (destructorBinding != null) {
                refs.put(destructorBinding, new ImplicitDestructorDeclarationReference(destructorBinding, name, file));
            }
        } else if (binding instanceof ITypedef) {
            IType type = ((ITypedef) binding).getType();
            if (type instanceof ICPPClassType) {
                processCandidate((IBinding) type, name);
            }
        } else if (binding instanceof IProblemBinding) {
            IProblemBinding problemBinding = (IProblemBinding) binding;
            for (IBinding candidate : problemBinding.getCandidateBindings()) {
                processCandidate(candidate, name);
            }
        }
    }

}
