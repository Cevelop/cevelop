package com.cevelop.gslator.checkers.visitors.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;


public class TypeIdVisitor extends ASTVisitor {

    List<IASTTypeId> typeIds;

    public TypeIdVisitor() {
        typeIds = new ArrayList<>();
        shouldVisitTypeIds = true;
    }

    @Override
    public int visit(final IASTTypeId typeId) {
        typeIds.add(typeId);
        return super.visit(typeId);
    }

    public List<IASTTypeId> getTypeIds() {
        return typeIds;
    }

}
