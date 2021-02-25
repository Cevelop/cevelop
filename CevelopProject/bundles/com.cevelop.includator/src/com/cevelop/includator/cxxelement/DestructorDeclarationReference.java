package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.includator.resources.IncludatorFile;


public class DestructorDeclarationReference extends SpecialMemberFunctionDeclarationReference {

    public DestructorDeclarationReference(ICPPMethod destructorBinding, IASTNode helperNode, IncludatorFile file) {
        super(destructorBinding, helperNode, file);
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return false;
    }

    @Override
    public String getName() {
        return getMethodBinding().getName();
    }

    public void addIsRefNameToMetaInfo() {
        isOnlyReferenceName = true;
    }

    @Override
    public boolean couldBePolymorphicMethodCall() {
        return isOnlyReferenceName();
    }

    @Override
    public boolean isImplicitDefinition() {
        return false;
    }

    @Override
    public SpecialMemberFunctionKind getKind() {
        return SpecialMemberFunctionKind.Destructor;
    }
}
