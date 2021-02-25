package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;

import com.cevelop.aliextor.ast.selection.IRefactorSelection;
import com.cevelop.aliextor.ast.selection.PartialRefactorSelection;
import com.cevelop.aliextor.ast.selection.PartialRefactorSelection.TQ_SC_PO;


public class PartialSelectionVisitor extends BaseASTVisitor {

    public PartialSelectionVisitor(IRefactorSelection refactorSelection) {
        super(refactorSelection);
        shouldVisitDeclSpecifiers = true;
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {

        TQ_SC_PO tqOfAlias = ((PartialRefactorSelection) refactorSelection).getTqOfAliasDecl();

        boolean prerequirements = (!tqOfAlias.isConst || declSpec.isConst()) && (!tqOfAlias.isVolatile || declSpec.isVolatile());

        ICPPASTDeclSpecifier minifiedDeclSpecOfFoundNode = ASTHelper.getDeclSpecWithoutTypeQualifiersAndStorageClass((ICPPASTDeclSpecifier) declSpec);

        ICPPASTDeclSpecifier minifiedDeclSpecOfSelectedNode = ASTHelper.getDeclSpecWithoutTypeQualifiersAndStorageClass(
                (ICPPASTDeclSpecifier) ((PartialRefactorSelection) refactorSelection).getDeclSpec());

        // Compare selected pointer Operators with found Nodes pointer Operators
        if (tqOfAlias.pointerOperators.size() > 0) {

            IASTPointerOperator pointerOperators[] = ASTHelper.findPointerOperators(declSpec);

            if (!areSamePointerOperators(tqOfAlias, pointerOperators)) {
                prerequirements = false;
            }
        }

        // Type doesn't match (eg. int and std::string)
        if (!minifiedDeclSpecOfFoundNode.toString().contentEquals(minifiedDeclSpecOfSelectedNode.toString())) {
            prerequirements = false;
        }

        if (prerequirements) {
            occurrences.add(declSpec);
        }

        return super.visit(declSpec);
    }

    private boolean areSamePointerOperators(TQ_SC_PO tqOfAlias, IASTPointerOperator[] pointerOperators) {
        if (pointerOperators != null && tqOfAlias.pointerOperators.size() <= pointerOperators.length) {
            if (tqOfAlias.pointerOperators.size() <= pointerOperators.length) {
                // Check if selected pointerOperators are also present
                for (int i = 0; i < tqOfAlias.pointerOperators.size(); i++) {
                    // Do the selected pointerOperators NOT match with the
                    // found Nodes pointerOperators?
                    if (!ASTHelper.isSamePointer(tqOfAlias.pointerOperators.get(i), pointerOperators[i])) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
