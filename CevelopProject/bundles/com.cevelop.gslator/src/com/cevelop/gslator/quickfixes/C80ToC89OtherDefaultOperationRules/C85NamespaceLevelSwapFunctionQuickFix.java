package com.cevelop.gslator.quickfixes.C80ToC89OtherDefaultOperationRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.ASTHelper;


public class C85NamespaceLevelSwapFunctionQuickFix extends BaseQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C85.getId())) {
            return Rule.C85 + ": Provide a namespace level swap function";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        ICPPASTNamespaceDefinition nameSpace = ASTHelper.getNamespace((IASTDeclaration) ASTHelper.getCompositeTypeSpecifier(markedNode).getParent());
        IASTDeclaration newNamespaceSwapFunction = ASTFactory.newNamespaceSwapFunction(((IASTName) markedNode).getLastName());

        if (nameSpace == null) {
            IASTTranslationUnit trans = markedNode.getTranslationUnit();
            hRewrite.insertBefore(trans, null, newNamespaceSwapFunction, null);
        } else {
            hRewrite.insertBefore(nameSpace, null, newNamespaceSwapFunction, null);
        }

    }

}
