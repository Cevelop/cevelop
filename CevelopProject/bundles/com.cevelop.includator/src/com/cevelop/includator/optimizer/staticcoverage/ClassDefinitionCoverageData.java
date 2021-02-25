package com.cevelop.includator.optimizer.staticcoverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.includator.cxxelement.ConstructorDeclarationReference;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.cxxelement.DestructorDeclarationReference;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.resources.IncludatorFile;


public class ClassDefinitionCoverageData {

    /**
     * True as value indicates that the given declRef is already covered. note that only constr and destr will be present here.
     */
    private List<DeclarationReference>       constrDestrs;
    private Collection<DeclarationReference> refs;
    private IASTNode                         classDefinitionNode;

    public ClassDefinitionCoverageData(IASTNode classDefinitionNode, IncludatorFile file) {
        this.classDefinitionNode = classDefinitionNode;
        refs = DeclarationReferenceHelper.findDeclReferences(classDefinitionNode, file);
        initConstrDestrs();
    }

    private void initConstrDestrs() {
        constrDestrs = new ArrayList<>();
        for (DeclarationReference curRef : refs) {
            if (curRef instanceof ConstructorDeclarationReference || curRef instanceof DestructorDeclarationReference) {
                constrDestrs.add(curRef);
            }
        }
    }

    public void setCovered(DeclarationReference constrOrDestrRef) {
        constrDestrs.remove(constrOrDestrRef);
    }

    public boolean areAllConstrAndDestrCovered() {
        return constrDestrs.isEmpty();
    }

    public boolean shouldCoverConstrOrDestr(DeclarationReference constrOrDestrRef) {
        return constrDestrs.contains(constrOrDestrRef);
    }

    public Collection<DeclarationReference> getDeclRefs() {
        return refs;
    }

    @Override
    public String toString() {
        return classDefinitionNode.getRawSignature();
    }
}
