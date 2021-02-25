package com.cevelop.constificator.core.deciders.decision;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;

import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Truelean;


public class NodeDecision implements Decision {

    private Truelean       decision = Truelean.NO;
    private final IASTNode node;

    public NodeDecision(IASTNode node) {
        this.node = node;
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
        if (!(node instanceof ICPPASTDeclarator)) {
            ICPPASTDeclarator declarator;
            if ((declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, node)) == null) {
                return null;
            } else {
                return declarator.getName();
            }
        } else {
            return ((ICPPASTDeclarator) node).getName();
        }
    }

    @Override
    public IASTNode node() {
        return node;
    }

    @Override
    public String note() {
        return null;
    }

}
