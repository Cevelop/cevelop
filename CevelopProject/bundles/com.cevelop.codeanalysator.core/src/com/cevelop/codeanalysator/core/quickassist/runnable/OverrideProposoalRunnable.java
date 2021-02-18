package com.cevelop.codeanalysator.core.quickassist.runnable;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.ASTCache.ASTRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cevelop.codeanalysator.core.util.VirtualHelper;


@SuppressWarnings("restriction")
public class OverrideProposoalRunnable implements ASTRunnable {

    private final int           selectionOffset;
    private final int           selectionLength;
    public IASTNode             node;
    public boolean              isOverridingFunctionDefinition       = false;
    public String               displayString;
    private static final String AddOverrideDisplayString             = "Add override keyword to function definition";
    private static final String ReplaceFinalDisplayString            = "Replace final keyword with override";
    private static final String RemoveVirtualSpecifiersDisplayString = "This method is not overriding. Remove virtual keywords";

    public OverrideProposoalRunnable(int selectionOffset, int selectionLength) {
        this.selectionOffset = selectionOffset;
        this.selectionLength = selectionLength;
    }

    @Override
    public IStatus runOnAST(ILanguage lang, IASTTranslationUnit ast) throws CoreException {
        node = ast.getNodeSelector(ast.getFilePath()).findEnclosingNode(selectionOffset, selectionLength);
        // This allows the quick assist to trigger over the whole function definition instead of just the parantheses.
        while (node != null) {
            if (node instanceof ICPPASTFunctionDefinition && isApplicable(node)) {
                return Status.OK_STATUS;
            }
            node = node.getParent();
        }
        return Status.CANCEL_STATUS;
    }

    private boolean isApplicable(IASTNode node) {
        ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) node;
        ICPPASTFunctionDeclarator declarator = (ICPPASTFunctionDeclarator) funcDef.getDeclarator();
        if (!isOverridingMethod(declarator)) {
            isOverridingFunctionDefinition = false;
            displayString = RemoveVirtualSpecifiersDisplayString;
            return (declarator.isOverride() || declarator.isFinal());
        }

        if (declarator.isFinal()) {
            isOverridingFunctionDefinition = true;
            displayString = ReplaceFinalDisplayString;
            return true;
        }

        if (isOverridingMethod(declarator)) {
            isOverridingFunctionDefinition = true;
            displayString = AddOverrideDisplayString;
            return true;
        }

        return false;
    }

    private boolean isOverridingMethod(ICPPASTFunctionDeclarator functionDeclarator) {
        IASTName name = functionDeclarator.getName();
        if (name != null) {
            IBinding binding = name.resolveBinding();
            if (binding instanceof ICPPMethod) {
                ICPPMethod method = (ICPPMethod) binding;
                return VirtualHelper.overridesVirtualMethod(method);
            }
        }
        return false;
    }
}
