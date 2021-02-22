package com.cevelop.includator.cxxelement;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


public class TypeDefDeclarationReference extends DeclarationReference {

    public TypeDefDeclarationReference(IBinding binding, IASTNode declarationReferenceNode, IncludatorFile file) {
        super(binding, declarationReferenceNode, file);
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (isClassTypeDef()) {
            ClassDeclarationReference.handleRefToClassCandidateSelection(definitions, declaration, this);
        } else {
            super.pickRequiredDependencies(definitions, declaration);
        }
    }

    private boolean isClassTypeDef() {
        ITypedef typeDefBinding = (ITypedef) getBinding();
        return typeDefBinding.getType() instanceof ICPPClassType;
    }
}
