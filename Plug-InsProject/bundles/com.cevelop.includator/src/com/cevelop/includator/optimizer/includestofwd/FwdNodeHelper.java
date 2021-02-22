package com.cevelop.includator.optimizer.includestofwd;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassTemplate;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ContainerNode;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.IncludatorException;
import com.cevelop.includator.helpers.IncludatorStatus;
import com.cevelop.includator.helpers.NodeHelper;


@SuppressWarnings("restriction")
public class FwdNodeHelper {

    private static final CPPNodeFactory nodeFactory = CPPNodeFactory.getDefault();

    public static IASTNode createCompositeForwardDeclarationFor(List<DeclarationReferenceDependency> candidatesToFwd) {
        ContainerNode containerNode = new ContainerNode();
        for (DeclarationReferenceDependency curDependency : candidatesToFwd) {
            addForwardDeclarationFor(curDependency, containerNode);
        }
        return containerNode;
    }

    private static void addForwardDeclarationFor(DeclarationReferenceDependency dependency, ContainerNode containerNode) {
        DeclarationReference ref = dependency.getDeclarationReference();
        if (ref.isClassReference()) {
            addClassForwardDeclaration(dependency, containerNode);
        } else if (ref.isFunctionReference()) {
            addFunctionFrowardDeclaration(dependency, containerNode);
        } else if (isTypeDefRef(ref)) {
            addTypeDefForwardDeclaration(dependency, containerNode);
        } else {
            IncludatorPlugin.logStatus(new IncludatorStatus(makeExceptionMessage(ref)), ref.getFile());
        }
    }

    private static void addTypeDefForwardDeclaration(DeclarationReferenceDependency dependency, ContainerNode containerNode) {
        IASTName originalDeclarationName = dependency.getDeclaration().getASTName();
        if (originalDeclarationName == null) {
            return;
        }
        IASTSimpleDeclaration declNode = NodeHelper.findParentOfType(IASTSimpleDeclaration.class, originalDeclarationName);
        if (declNode != null) {
            containerNode.addNode(declNode.copy());
        } else {
            String mgs = "Failed to add typedef node for " + dependency.getDeclaration() + ". No surrounding typedef declaration found.";
            IncludatorPlugin.logStatus(new IncludatorStatus(mgs), dependency.getDeclarationReference().getFile());
        }
    }

    private static boolean isTypeDefRef(DeclarationReference ref) {
        return ref.getBinding() instanceof ITypedef;
    }

    private static void addFunctionFrowardDeclaration(DeclarationReferenceDependency dependency, ContainerNode containerNode) {
        DeclarationReference ref = dependency.getDeclarationReference();
        IBinding binding = ref.getBinding();
        IASTName originalDeclarationName = dependency.getDeclaration().getASTName();
        IASTDeclaration fwdNode = createFunctionFwdDecl(originalDeclarationName);
        if (fwdNode == null) {
            throw new IncludatorException(makeExceptionMessage(ref), dependency.getDeclarationReference().getFile().getSmartPath());
        }

        if (binding instanceof ICPPTemplateInstance) {
            fwdNode = wrapWithTemplateArguments(fwdNode, originalDeclarationName);
        }
        containerNode.addNode(fwdNode);
    }

    private static IASTSimpleDeclaration createFunctionFwdDecl(IASTName originalDeclarationName) {
        IASTFunctionDeclarator originalDeclarator = NodeHelper.findParentOfType(IASTFunctionDeclarator.class, originalDeclarationName);
        IASTNode declaration = originalDeclarator.getParent();
        IASTSimpleDeclaration fwdNode = null;
        if (declaration instanceof IASTSimpleDeclaration) {
            fwdNode = (IASTSimpleDeclaration) declaration; // declaration is already a fwd declaration
        } else if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition definition = (IASTFunctionDefinition) declaration;
            IASTSimpleDeclaration newDecl = nodeFactory.newSimpleDeclaration(definition.getDeclSpecifier().copy());
            newDecl.addDeclarator(definition.getDeclarator().copy());
            fwdNode = newDecl;
        }
        return fwdNode;
    }

    private static ICPPASTTemplateDeclaration wrapWithTemplateArguments(IASTDeclaration declarationToWrap, IASTName originalDeclarationName) {
        ICPPASTTemplateDeclaration originalTemplageDeclaration = NodeHelper.findParentOfType(ICPPASTTemplateDeclaration.class,
                originalDeclarationName);
        ICPPASTTemplateDeclaration newDecl = originalTemplageDeclaration.copy();
        newDecl.setDeclaration(declarationToWrap);
        return newDecl;
    }

    private static void addClassForwardDeclaration(DeclarationReferenceDependency dependency, ContainerNode containerNode) {
        DeclarationReference ref = dependency.getDeclarationReference();
        try {
            IASTName originalDeclarationName = dependency.getDeclaration().getASTName();
            if (originalDeclarationName == null) {
                return;
            }
            IASTDeclaration declaration = createSimpleClassFwd(originalDeclarationName, dependency);
            if (ref.getBinding() instanceof ICPPClassTemplate) {
                declaration = wrapWithTemplateArguments(declaration, originalDeclarationName);
            }
            containerNode.addNode(declaration);
        } catch (DOMException e) {
            IncludatorPlugin.logStatus(new IncludatorStatus(makeExceptionMessage(ref)), ref.getFile());
        }
    }

    private static IASTSimpleDeclaration createSimpleClassFwd(IASTName originalDeclarationName, DeclarationReferenceDependency dependency)
            throws DOMException {
        IASTName name = originalDeclarationName.copy();
        int kind = ((ICPPClassType) dependency.getDeclarationReference().getBinding()).getKey();
        ICPPASTElaboratedTypeSpecifier declSpecifier = nodeFactory.newElaboratedTypeSpecifier(kind, name);
        IASTSimpleDeclaration decl = nodeFactory.newSimpleDeclaration(declSpecifier);
        return decl;
    }

    private static String makeExceptionMessage(DeclarationReference ref) {
        return "Failed to create forward declaration for: " + ref.toString();
    }
}
