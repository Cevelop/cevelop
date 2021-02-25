package com.cevelop.includator.cxxelement;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

import com.cevelop.includator.resources.IncludatorFile;


public class VariableDeclarationReference extends DeclarationReference {

    public VariableDeclarationReference(IBinding binding, IASTNode declarationReferenceNode, IncludatorFile file) {
        super(binding, declarationReferenceNode, file);
    }

    @Override
    protected boolean evalIsForwardDeclEnough(IASTName name) {
        return false;
    }
}
