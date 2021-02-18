package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.autosar.util.DeclaratorHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;
import com.cevelop.codeanalysator.core.util.VirtualHelper;


public class VirtualFunctionShallHaveExactlyOneSpecifierQuickFix extends BaseQuickFix {

    public VirtualFunctionShallHaveExactlyOneSpecifierQuickFix(String label) {
        super(label);
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;

        String contextFlagsString = getProblemArgument(marker, ContextFlagsHelper.VirtualFunctionShallHaveExactlyOneSpecifierContextFlagsStringIndex);
        return !contextFlagsString.contains(ContextFlagsHelper.VirtualFunctionShallHaveExactlyOneSpecifierContextFlagPureVirtual) //
               && !contextFlagsString.contains(ContextFlagsHelper.VirtualFunctionShallHaveExactlyOneSpecifierContextFlagIntroducingFinal);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTFunctionDeclarator)) return;
        IASTFunctionDeclarator declarator = (IASTFunctionDeclarator) markedNode;
        ICPPASTFunctionDeclarator decl = (ICPPASTFunctionDeclarator) declarator;

        IASTNode parent = markedNode.getParent();
        IASTDeclSpecifier declSpec = DeclaratorHelper.findDeclSpec(parent);
        if (!(declSpec instanceof ICPPASTSimpleDeclSpecifier)) {
            throw new IllegalArgumentException();
        }
        ICPPASTSimpleDeclSpecifier simpleDeclSpec = (ICPPASTSimpleDeclSpecifier) declSpec;

        ICPPASTSimpleDeclSpecifier newSimpleDeclSpec = null;
        ICPPASTFunctionDeclarator newDeclarator = null;

        IASTName name = decl.getName();
        IBinding binding = name.resolveBinding();
        if (!(binding instanceof ICPPMethod)) return;
        ICPPMethod method = (ICPPMethod) binding;

        if (decl.isFinal()) {
            newDeclarator = DeclaratorHelper.createFunctionDeclarator(factory, decl.copy(), SpecifierKind.Final);
            newSimpleDeclSpec = createSimpleDeclSpecifier(factory, simpleDeclSpec, false);

        } else if (VirtualHelper.overridesVirtualMethod(method)) {
            newDeclarator = DeclaratorHelper.createFunctionDeclarator(factory, decl.copy(), SpecifierKind.Override);
            newSimpleDeclSpec = createSimpleDeclSpecifier(factory, simpleDeclSpec, false);
        } else {
            newDeclarator = DeclaratorHelper.createFunctionDeclarator(factory, decl.copy(), null);
            newSimpleDeclSpec = createSimpleDeclSpecifier(factory, simpleDeclSpec, true);
        }

        hRewrite.replace(declarator, newDeclarator, null);

        hRewrite.replace(simpleDeclSpec, newSimpleDeclSpec, null);
    }

    private ICPPASTSimpleDeclSpecifier createSimpleDeclSpecifier(ICPPNodeFactory factory, IASTSimpleDeclSpecifier simpleDeclSpec, boolean virtual) {
        ICPPASTSimpleDeclSpecifier icppastSimpleDeclSpecifier = factory.newSimpleDeclSpecifier();
        icppastSimpleDeclSpecifier.setType(simpleDeclSpec.getType());
        icppastSimpleDeclSpecifier.setVirtual(virtual);
        return icppastSimpleDeclSpecifier;
    }
}
