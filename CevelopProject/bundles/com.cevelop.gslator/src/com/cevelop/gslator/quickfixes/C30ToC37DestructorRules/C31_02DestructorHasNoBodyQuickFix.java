package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.utils.OwnerInformation;


public class C31_02DestructorHasNoBodyQuickFix extends C31_00DeleteOwnersInDestructorQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C31_02.getId())) return Rule.C31 + ": Add body to destructor and insert delete statements";
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final ASTRewrite rewrite, final ICPPASTCompositeTypeSpecifier struct,
            final List<OwnerInformation> ownerInformation, final IASTDeclaration destructor) {
        final ICPPASTFunctionDefinition newdestructor = getFunctionDefinition(destructor);

        configNewDestructor(ownerInformation, newdestructor);

        if (destructor instanceof IASTSimpleDeclaration) {
            final ICPPASTFunctionDefinition funcDef = getImplFromDeclaration((IASTSimpleDeclaration) destructor);

            astRewriteStore.getASTRewrite(funcDef).replace(funcDef, newdestructor, null);
        } else if (destructor instanceof ICPPASTFunctionDefinition) {
            rewrite.replace(destructor, newdestructor, null);
        }
    }

    @SuppressWarnings("restriction")
    private void configNewDestructor(final List<OwnerInformation> ownerInformation, final ICPPASTFunctionDefinition newdestructor) {
        newdestructor.setIsDefaulted(false);
        newdestructor.setIsDeleted(false);
        newdestructor.setBody(factory.newCompoundStatement());

        addMissingDeleteStatements(newdestructor, ownerInformation);
    }

}
