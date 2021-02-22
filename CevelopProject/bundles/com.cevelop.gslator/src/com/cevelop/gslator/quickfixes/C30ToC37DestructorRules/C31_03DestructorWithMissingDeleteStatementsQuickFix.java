package com.cevelop.gslator.quickfixes.C30ToC37DestructorRules;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.gslator.checkers.visitors.util.DeleteStatementsVisitor;
import com.cevelop.gslator.checkers.visitors.util.NameVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.ids.IdHelper.Rule;
import com.cevelop.gslator.quickfixes.BaseQuickFix;
import com.cevelop.gslator.utils.OwnerInformation;


public class C31_03DestructorWithMissingDeleteStatementsQuickFix extends C31_00DeleteOwnersInDestructorQuickFix {

    public C31_03DestructorWithMissingDeleteStatementsQuickFix() {}

    @Override
    public String getLabel() {
        final String problemId = getProblemId(marker);
        if (problemId.contentEquals(ProblemId.P_C31_03.getId())) {
            return Rule.C31 + ": Add delete statements to destructor body";
        }
        return BaseQuickFix.FAIL;
    }

    @Override
    protected void handleMarkedNode(final ASTRewrite rewrite, final ICPPASTCompositeTypeSpecifier struct,
            final List<OwnerInformation> ownerInformation, final IASTDeclaration destructor) {
        final DeleteStatementsVisitor deleteStatementsVisitor = new DeleteStatementsVisitor();
        final NameVisitor nameVisitor = new NameVisitor();
        ICPPASTFunctionDefinition newdestructor = getFunctionDefinition(destructor);

        newdestructor.accept(deleteStatementsVisitor);
        findDeletStatements(deleteStatementsVisitor, nameVisitor);
        removeExistingDelStatmts(ownerInformation, nameVisitor);

        newdestructor = addMissingDeleteStatements(newdestructor, ownerInformation);

        if (destructor instanceof IASTSimpleDeclaration) {
            final ICPPASTFunctionDefinition funcDef = getImplFromDeclaration((IASTSimpleDeclaration) destructor);
            astRewriteStore.getASTRewrite(funcDef).replace(funcDef, newdestructor, null);
        } else if (destructor instanceof ICPPASTFunctionDefinition) {
            rewrite.replace(destructor, newdestructor, null);
        }

    }

    private void findDeletStatements(final DeleteStatementsVisitor deleteStatementsVisitor, final NameVisitor nameVisitor) {
        for (final ICPPASTDeleteExpression deleteExpression : deleteStatementsVisitor.getDeleteExpressions()) {
            deleteExpression.accept(nameVisitor);
        }
    }

    private void removeExistingDelStatmts(final List<OwnerInformation> ownerInformation, final NameVisitor nameVisitor) {
        for (final IASTName name : nameVisitor.getNames()) {
            ownerInformation.remove(new OwnerInformation(name.copy()));
        }
    }

}
