package com.cevelop.gslator.checkers.visitors.util;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;


public class PointerFromDeclarationVisitor extends ASTVisitor {

    boolean isOwningRef;

    public PointerFromDeclarationVisitor() {
        shouldVisitPointerOperators = true;
    }

    @Override
    public int visit(final IASTPointerOperator ptrOperator) {
        isOwningRef = ptrOperator instanceof ICPPASTReferenceOperator ? true : false;
        return PROCESS_ABORT;
    }

    public boolean isOwningRef() {
        return isOwningRef;
    }

}
