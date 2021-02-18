package com.cevelop.constificator.core.deciders.decision;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.constificator.core.util.type.Truelean;


public class MemberFunctionDecision implements Decision {

    private Truelean                     decision = Truelean.NO;
    private final IASTFunctionDeclarator declarator;
    private boolean                      shadows  = false;

    public MemberFunctionDecision(IASTFunctionDeclarator function) {
        declarator = function;
    }

    @Override
    public void decide(Truelean decision) {
        this.decision = decision;
    }

    @Override
    public Truelean get() {
        return decision;
    }

    @Override
    public IASTName name() {
        return declarator.getName();
    }

    @Override
    public IASTNode node() {
        return declarator;
    }

    @Override
    public String note() {
        String note = "";

        if (decision == Truelean.MAYBE) {
            note = String.format("Adding const qualification will %s '%s' in at least one of its base classes", shadows ? "shadow" : "override",
                    name().toString());
        }

        return note;
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
    }

}
