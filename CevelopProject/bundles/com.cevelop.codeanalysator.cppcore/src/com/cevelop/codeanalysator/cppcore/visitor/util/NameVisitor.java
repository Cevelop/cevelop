package com.cevelop.codeanalysator.cppcore.visitor.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTName;


public class NameVisitor extends ASTVisitor {

    List<IASTName> names;

    public NameVisitor() {
        names = new ArrayList<IASTName>();
        shouldVisitNames = true;
    }

    @Override
    public int visit(final IASTName name) {
        names.add(name);
        return super.visit(name);
    }

    public List<IASTName> getNames() {
        return names;
    }

}
