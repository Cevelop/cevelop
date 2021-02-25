package com.cevelop.constificator.core.deciders.decision;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import com.cevelop.constificator.core.util.type.Truelean;


public class NullDecision implements Decision {

    @Override
    public void decide(Truelean decision) {}

    @Override
    public Truelean get() {
        return Truelean.NO;
    }

    @Override
    public IASTName name() {
        return null;
    }

    @Override
    public IASTNode node() {
        return null;
    }

    @Override
    public String note() {
        return "";
    }

}
