package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.includator.resources.IncludatorFile;


public class ImplicitDestructorDeclarationReference extends DestructorDeclarationReference {

    private final boolean isInitialNameDefinition;

    public ImplicitDestructorDeclarationReference(ICPPMethod destructorBinding, IASTName helperName, IncludatorFile file) {
        this(destructorBinding, helperName, file, false);
    }

    public ImplicitDestructorDeclarationReference(ICPPMethod destructorBinding, IASTName helperName, IncludatorFile file, boolean isDefinition) {
        super(destructorBinding, helperName, file);
        this.isInitialNameDefinition = isDefinition;
    }

    @Override
    protected void initFlags(IASTNode declarationReferenceNode) {
        problemWhileResolving = false;
        isOnlyReferenceName = !isInitialNameDefinition;
        isOnlyDeclarationName = false;
        isDefinitionName = isInitialNameDefinition;
        isFrowardDeclEnough = false;
        isQualifiedName = false;
        isUnqualifiedName = true;
    }

    @Override
    public boolean isImplicitDefinition() {
        return isDefinitionName();
    }
}
