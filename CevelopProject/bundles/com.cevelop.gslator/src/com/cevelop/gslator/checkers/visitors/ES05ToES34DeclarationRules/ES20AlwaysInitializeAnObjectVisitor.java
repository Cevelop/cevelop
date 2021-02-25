package com.cevelop.gslator.checkers.visitors.ES05ToES34DeclarationRules;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPVariable;

import com.cevelop.gslator.checkers.BaseChecker;
import com.cevelop.gslator.checkers.visitors.BaseVisitor;
import com.cevelop.gslator.ids.IdHelper.ProblemId;
import com.cevelop.gslator.infos.GslatorInfo;


public class ES20AlwaysInitializeAnObjectVisitor extends BaseVisitor {

    public ES20AlwaysInitializeAnObjectVisitor(BaseChecker checker) {
        super(checker);
    }

    @Override
    protected void setShouldVisit() {
        shouldVisitDeclarations = true;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {
        if (declaration.getPropertyInParent() == ICPPASTRangeBasedForStatement.DECLARATION) {
            return PROCESS_CONTINUE;
        }
        if (declaration instanceof IASTSimpleDeclaration) {
            for (IASTDeclarator declarator : ((IASTSimpleDeclaration) declaration).getDeclarators()) {
                IBinding nameBinding = declarator.getName().resolveBinding();

                if (declarator.getInitializer() == null && nodeHasNoIgnoreAttribute(this, declarator) && nameBinding instanceof ICPPVariable &&
                    !(declaration.getParent() instanceof ICPPASTCatchHandler) && !(nameBinding instanceof ICPPField)) {

                    checker.reportProblem(ProblemId.P_ES20, declarator, new GslatorInfo("Always initialize an object"));
                }
            }
        }

        return super.visit(declaration);
    }
}
