package com.cevelop.includator.cxxelement;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;

import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class ClassDeclarationReference extends DeclarationReference {

    private final ICPPClassType classBinding;

    public ClassDeclarationReference(ICPPClassType binding, IASTNode declarationReferenceNode, IncludatorFile file) {
        super(binding, declarationReferenceNode, file);
        classBinding = binding;
    }

    @Override
    public boolean isClassReference() {
        return true;
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return isOnlyDeclarationName() || NodeHelper.isForwardDeclarationEnough(name);
    }

    public ICPPClassType getClassBinding() {
        return classBinding;
    }

    @Override
    public IBinding getBinding() {
        return classBinding;
    }

    @Override
    public boolean isImplicitRef() {
        return super.isImplicitRef() || (classBinding == null || haveSameBinding());
    }

    /**
     * checks whether the actual class binding is the same as the name's binding of "this". this is not the case refs which do not have an own name.
     * E.g. a constructor ref while there the constructor is not defined has the class' name as helper name.
     *
     * @return
     */
    private boolean haveSameBinding() {
        return (declarationReferenceNode instanceof IASTName) ? !classBinding.equals(((IASTName) declarationReferenceNode).resolveBinding()) : false;
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        handleRefToClassCandidateSelection(definitions, declaration, this);
    }

    public static void handleRefToClassCandidateSelection(List<DeclarationReferenceDependency> definitions,
            DeclarationReferenceDependency declaration, DeclarationReference ref) {
        if (declaration != null && ref.isForwardDeclarationEnough()) {
            ref.requiredDependencies.add(declaration);
        } else if (!definitions.isEmpty()) {
            ref.handlePickRequiredFromDefs(definitions);
        }
    }

    @Override
    public String getName() {
        if (declarationReferenceNode instanceof IASTExpression) {
            return "type " + classBinding.getName() + " of expression '" + super.getName() + "'";
        }
        return super.getName();
    }
}
