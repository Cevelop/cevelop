package com.cevelop.codeanalysator.autosar.quickfix;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.core.resources.IMarker;

import com.cevelop.codeanalysator.autosar.util.ContextFlagsHelper;
import com.cevelop.codeanalysator.autosar.util.DeclaratorHelper;
import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;


public class DoNotIntroduceVirtualFunctionInFinalClassQuickFix extends BaseQuickFix {

    public DoNotIntroduceVirtualFunctionInFinalClassQuickFix(String label) {
        super(label);
    }

    @Override
    public boolean isApplicable(IMarker marker) {
        if (!super.isApplicable(marker)) return false;
        String contextFlagsString = getProblemArgument(marker, ContextFlagsHelper.DoNotIntroduceVirtualFunctionInFinalClassContextFlagsStringIndex);
        return !contextFlagsString.contains(ContextFlagsHelper.DoNotIntroduceVirtualFunctionInFinalClassContextFlagPureVirtual) //
               && !contextFlagsString.contains(ContextFlagsHelper.DoNotIntroduceVirtualFunctionInFinalClassContextFlagIntroducingVirtual);
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        if (!(markedNode instanceof ICPPASTFunctionDeclarator)) return;
        IASTNode parent = markedNode.getParent();

        IASTDeclSpecifier declSpec = DeclaratorHelper.findDeclSpec(parent);
        IASTFunctionDeclarator funcDeclarator = (IASTFunctionDeclarator) markedNode;

        IASTDeclSpecifier newDeclSpecifier = createDeclSpec(declSpec);

        ICPPASTFunctionDeclarator newFunctionDeclarator = DeclaratorHelper.createFunctionDeclarator(factory, funcDeclarator, SpecifierKind.Final);

        hRewrite.replace(declSpec, newDeclSpecifier, null);
        hRewrite.replace(funcDeclarator, newFunctionDeclarator, null);
    }

    private IASTDeclSpecifier createDeclSpec(IASTDeclSpecifier declSpec) {
        if (declSpec instanceof ICPPASTSimpleDeclSpecifier) {
            ICPPASTSimpleDeclSpecifier simpleDeclSpec = (ICPPASTSimpleDeclSpecifier) declSpec;
            ICPPASTSimpleDeclSpecifier newSimpleDeclSpec = factory.newSimpleDeclSpecifier();
            newSimpleDeclSpec.setType(simpleDeclSpec.getType());
            return newSimpleDeclSpec;
        } else if (declSpec instanceof ICPPASTNamedTypeSpecifier) {
            ICPPASTNamedTypeSpecifier namedSpec = (ICPPASTNamedTypeSpecifier) declSpec;
            ICPPASTNamedTypeSpecifier newNamedSpec = namedSpec.copy();
            newNamedSpec.setVirtual(false);
            return newNamedSpec;
        }
        throw new IllegalArgumentException("Passed invalid decl specifier");
    }
}
