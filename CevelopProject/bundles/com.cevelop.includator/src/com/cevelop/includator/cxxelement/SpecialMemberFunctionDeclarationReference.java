package com.cevelop.includator.cxxelement;

import java.util.List;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.BindingHelper;
import com.cevelop.includator.helpers.DeclarationReferenceHelper;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.resources.IncludatorProject;


public abstract class SpecialMemberFunctionDeclarationReference extends MethodDeclarationReference {

    public static enum SpecialMemberFunctionKind {
        DefaultConstructor, CopyConstructor, OtherConstructor, Destructor
    }

    private boolean resolvesToClass;

    public SpecialMemberFunctionDeclarationReference(ICPPMethod methodBinding, IASTNode node, IncludatorFile file) {
        super(methodBinding, node, file);
    }

    public abstract boolean isImplicitDefinition();

    public abstract SpecialMemberFunctionKind getKind();

    @Override
    public void initAllDependencies() {
        List<DeclarationReferenceDependency> dependencyList = DeclarationReferenceHelper.getDependencies(this);
        handleIsEmpty(dependencyList);
        resolvesToClass = resolvesToClass(dependencyList);
        allDependencies = dependencyList;
    }

    @Override
    protected void pickRequiredDependencies(List<DeclarationReferenceDependency> definitions, DeclarationReferenceDependency declaration) {
        if (resolvesToClass) {
            handlePickRequiredFromDefs(definitions);
        } else if (declaration != null) {
            requiredDependencies.add(declaration);
        } else if (!definitions.isEmpty()) {
            handlePickRequiredFromDefs(definitions);
        }
    }

    private boolean resolvesToClass(List<DeclarationReferenceDependency> dependencyList) {
        if (dependencyList.isEmpty()) {
            return false;
        }
        Declaration declaration = dependencyList.get(0).getDeclaration();
        try {
            return declaration.getFile().getProject().getIndex().findBinding(declaration.getName()) instanceof ICPPClassType;
        } catch (CoreException e) {
            return false;
        }
    }

    private void handleIsEmpty(List<DeclarationReferenceDependency> dependencyList) {
        if (dependencyList.isEmpty()) {
            addClassDefinitionDependency(dependencyList);
        }
    }

    private void addClassDefinitionDependency(List<DeclarationReferenceDependency> dependencies) {
        ICPPClassType classType = getMethodBinding().getClassOwner();
        classType = BindingHelper.getMostSpecificDefiningClassBinding(classType, file.getProject().getIndex());
        IncludatorProject project = getFile().getProject();
        IIndex index = project.getIndex();
        try {
            IIndexName[] declNames = index.findNames(classType, IIndex.FIND_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES);
            for (IName curName : declNames) {
                IncludatorFile file = project.getFile(curName.getFileLocation().getFileName());
                Declaration declaration = IncludatorPlugin.getDeclarationStore().getDeclaration(curName, file);
                dependencies.add(new DeclarationReferenceDependency(this, declaration));
            }
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }
}
