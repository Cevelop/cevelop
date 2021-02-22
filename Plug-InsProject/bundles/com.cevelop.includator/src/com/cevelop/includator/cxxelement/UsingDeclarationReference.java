package com.cevelop.includator.cxxelement;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


public class UsingDeclarationReference extends DeclarationReference {

    public UsingDeclarationReference(IBinding binding, IASTNode declarationReferenceNode, IncludatorFile file) {
        super(binding, declarationReferenceNode, file);
    }

    @Override
    protected void initFlags(IASTNode declarationReferenceNode) {
        problemWhileResolving = false;
        isOnlyReferenceName = true;
        isOnlyDeclarationName = false;
        isDefinitionName = false;
        isFrowardDeclEnough = true;
        isQualifiedName = false;
        isUnqualifiedName = false;
    }

    @Override
    public void combineMetaInfoOfNameWith(IASTName otherName) {
        isFrowardDeclEnough = isForwardDeclarationEnough() && evalIsForwardDeclEnough(otherName);
        boolean isOtherQualifiedName = evalIsQualifiedName(otherName);
        isQualifiedName = isQualifiedName || isOtherQualifiedName;
        isUnqualifiedName = isUnqualifiedName || !isOtherQualifiedName;
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        for (DeclarationReferenceDependency dependency : definitions) {
            if (dependency.isSelfDependency()) {
                definitions.remove(dependency);
                break;
            }
        }
        super.pickRequiredDependencies(definitions, declaration);
    }

    @Override
    public List<DeclarationReferenceDependency> getIncludedDependencies() {
        List<DeclarationReferenceDependency> filteredIncludedDependencies = super.getIncludedDependencies();
        for (DeclarationReferenceDependency dependency : filteredIncludedDependencies) {
            if (dependency.isSelfDependency()) {
                filteredIncludedDependencies.remove(dependency);
                break;
            }
        }
        return filteredIncludedDependencies;
    }
}
