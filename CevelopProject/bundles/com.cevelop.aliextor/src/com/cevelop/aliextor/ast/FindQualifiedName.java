package com.cevelop.aliextor.ast;

import org.eclipse.cdt.core.dom.ast.IASTTypeId;

import com.cevelop.aliextor.ast.selection.IRefactorSelection;


public class FindQualifiedName extends TemplateAliasVisitor {

    boolean notFirstVisit;

    public FindQualifiedName(IRefactorSelection refactorSelection) {
        super(refactorSelection);
        notFirstVisit = false;
    }

    @Override
    public int visit(IASTTypeId typeId) {
        if (notFirstVisit) {
            return PROCESS_ABORT;
        } else {
            notFirstVisit = true;
            return PROCESS_CONTINUE;
        }
    }

}
