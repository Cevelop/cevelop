package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;

import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class MethodDeclarationReference extends FunctionDeclarationReference {

    private ICPPMethod methodBinding;
    private boolean    couldBePolymorphicMethodCall;

    public MethodDeclarationReference(ICPPMethod methodBinding, IASTNode node, IncludatorFile file) {
        super(methodBinding, node, file);
        this.methodBinding = methodBinding;
    }

    @Override
    protected void initFlags(IASTNode node) {
        couldBePolymorphicMethodCall = false;
        super.initFlags(node);
    }

    public ICPPMethod getMethodBinding() {
        return methodBinding;
    }

    public void setIsDefinitionName() {
        isDefinitionName = true;
    }

    public boolean couldBePolymorphicMethodCall() {
        return couldBePolymorphicMethodCall;
    }

    @Override
    public void combineMetaInfoOfNameWith(IASTName otherName) {
        super.combineMetaInfoOfNameWith(otherName);
        couldBePolymorphicMethodCall = couldBePolymorphicMethodCall || NodeHelper.couldBePolymorphicMethodCall(otherName);
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return false;
    }

    @Override
    public IBinding getBinding() {
        return methodBinding;
    }

    @Override
    public boolean isImplicitRef() {
        return super.isImplicitRef() || (methodBinding == null || haveSameBinding());
    }

    /**
     * checks whether the actual class binding is the same as the name's binding of "this". this is not the case refs which do not have an own name.
     * E.g. a constructor ref while there the constructor is not defined has the class' name as helper name.
     *
     * @return
     */
    private boolean haveSameBinding() {
        return (declarationReferenceNode instanceof IASTName) ? !methodBinding.equals(((IASTName) declarationReferenceNode).resolveBinding()) : false;
    }

    @Override
    public void clear() {
        methodBinding = null;
        super.clear();
    }
}
