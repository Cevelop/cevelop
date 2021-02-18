package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.quickfixes.utils.ASTFactory;
import com.cevelop.gslator.utils.OwnerInformation;


public class C31_01NoDestructorQuickFix extends C31_00DeleteOwnersInDestructorQuickFix {

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C31_01.getId())) return Rule.C31.toString() + ": Add destructor with delete statements";
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final ASTRewrite rewrite, final ICPPASTCompositeTypeSpecifier struct,
            final List<OwnerInformation> ownerInformation, final IASTDeclaration destructor) {
        if (destructor instanceof IASTSimpleDeclaration) {
            rewrite.remove(destructor, null);
        }

        ICPPASTFunctionDefinition newdestructor = ASTFactory.newNoexceptDestructor(struct.getName().copy());
        newdestructor = addMissingDeleteStatements(newdestructor, ownerInformation);

        final List<IASTNode> nodes = new ArrayList<>();
        nodes.add(newdestructor);
        insertNodesUnderVisibilityLabel(rewrite, struct, nodes, ICPPASTVisibilityLabel.v_public);
    }

}
