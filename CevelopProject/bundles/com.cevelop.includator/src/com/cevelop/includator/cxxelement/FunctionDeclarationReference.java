package com.cevelop.includator.cxxelement;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


// why is ICPPUnknownBinding cdt-internal???
@SuppressWarnings("restriction")
public class FunctionDeclarationReference extends DeclarationReference {

    boolean isUnqualifiedDefinition;

    public FunctionDeclarationReference(IFunction functionBinding, IASTNode declarationReferenceNode, IncludatorFile file) {
        super(functionBinding, declarationReferenceNode, file);
    }

    @Override
    protected void initFlags(IASTNode name) {
        isUnqualifiedDefinition = false;
        super.initFlags(name);
    }

    @Override
    public void combineMetaInfoOfNameWith(IASTName otherName) {
        super.combineMetaInfoOfNameWith(otherName);
        isUnqualifiedDefinition = isUnqualifiedDefinition || (otherName.isDefinition() && !evalIsQualifiedName(otherName));
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (isDefinitionName()) {
            if (pickRequiredPreferUnqualifiedDef(definitions, declaration)) {
                return;
            }
        }

        if (isOnlyDeclarationName()) {
            if (declaration != null) {
                requiredDependencies.add(declaration);
            } else {
                handlePickRequiredFromDefs(definitions);
            }
            return;
        }

        if (isOnlyReferenceName()) {
            if (isNameQualifiedName()) {
                if (pickRequiredPreferUnqualifiedDefForQualifiedReference(definitions, declaration)) {
                    return;
                }
            } else {
                if (pickRequiredPreferDecl(definitions, declaration)) {
                    return;
                }
            }
        }
    }

    private boolean pickRequiredPreferDecl(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (declaration != null) {
            requiredDependencies.add(declaration);
            return true;
        }
        if (!definitions.isEmpty()) {
            handlePickRequiredFromDefs(definitions);
            return true;
        }
        return false;
    }

    private boolean pickRequiredPreferUnqualifiedDef(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (isUnqualifiedDefinition) {
            if (!definitions.isEmpty()) {
                handlePickRequiredFromDefs(definitions);
                return true;
            }
        } else {
            if (declaration != null) {
                requiredDependencies.add(declaration);
            }
        }
        return false;
    }

    private boolean pickRequiredPreferUnqualifiedDefForQualifiedReference(List<DeclarationReferenceDependency> definitions,
            DeclarationReferenceDependency declaration) {
        if (isUnqualifiedDefinition) {
            if (!definitions.isEmpty()) {
                handlePickRequiredFromDefs(definitions);
                return true;
            }
        } else {
            if (declaration != null) {
                requiredDependencies.add(declaration);
            } else {
                handlePickRequiredFromDefs(definitions);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return true;
    }

    @Override
    public boolean isFunctionReference() {
        return true;
    }

    @Override
    protected void handlePickRequiredFromDefs(List<DeclarationReferenceDependency> definitions) {
        if (binding instanceof ICPPUnknownBinding) { // is called in template scope and should add all definitions. Needs to be fixed in CDT
            requiredDependencies.addAll(definitions);
        } else {
            super.handlePickRequiredFromDefs(definitions);
        }
    }
}
