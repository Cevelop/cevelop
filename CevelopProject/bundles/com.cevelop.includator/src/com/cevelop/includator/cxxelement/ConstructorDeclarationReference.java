package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;

import com.cevelop.includator.helpers.ConstructorReferenceHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class ConstructorDeclarationReference extends SpecialMemberFunctionDeclarationReference {

    private ICPPConstructor constructorBinding;

    private final boolean isImplicit;

    public ConstructorDeclarationReference(ICPPConstructor constructorBinding, IASTNode ctorNode, IncludatorFile file) {
        this(constructorBinding, ctorNode, file, false);
    }

    public ConstructorDeclarationReference(ICPPConstructor constructorBinding, IASTNode ctorName, IncludatorFile file, boolean isImplicit) {
        super(constructorBinding, ctorName, file);

        IBinding targetBinding = constructorBinding;

        while (targetBinding instanceof ICPPSpecialization) {
            targetBinding = ((ICPPSpecialization) targetBinding).getSpecializedBinding();
        }
        if (targetBinding instanceof ICPPConstructor) {
            this.constructorBinding = (ICPPConstructor) targetBinding;
        } else {
            this.constructorBinding = constructorBinding;
        }

        this.isImplicit = isImplicit;
        if (isImplicit) {
            problemWhileResolving = false;
            isOnlyReferenceName = true;
            isOnlyDeclarationName = false;
            isDefinitionName = false;
            isFrowardDeclEnough = false;
            isQualifiedName = false;
            isUnqualifiedName = true;
        }
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return false;
    }

    public boolean isCopyConstructor() throws DOMException {
        return ConstructorReferenceHelper.isCopyConstructor(constructorBinding, constructorBinding.getClassOwner());
    }

    public ICPPConstructor getConstructorBinding() {
        return constructorBinding;
    }

    @Override
    public void clear() {
        constructorBinding = null;
        super.clear();
    }

    @Override
    public String getName() {
        return constructorBinding.getName();
    }

    public void addIsRefNameToMetaInfo() {
        isOnlyReferenceName = true;
    }

    public boolean isDefaultConstructor() throws DOMException {
        return ConstructorReferenceHelper.isDefaultConstructor(constructorBinding);
    }

    @Override
    public boolean couldBePolymorphicMethodCall() {
        return isOnlyReferenceName();
    }

    @Override
    public boolean isImplicitDefinition() {
        return isImplicit && isDefinitionName();
    }

    public boolean isImplicit() {
        return isImplicit;
    }

    @Override
    public SpecialMemberFunctionKind getKind() {
        try {
            if (isDefaultConstructor()) {
                return SpecialMemberFunctionKind.DefaultConstructor;
            } else if (isCopyConstructor()) {
                return SpecialMemberFunctionKind.CopyConstructor;
            }
        } catch (DOMException e) {
            // Ignore exception
        }
        return SpecialMemberFunctionKind.OtherConstructor;
    }
}
