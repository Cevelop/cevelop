package com.cevelop.templator.plugin.asttools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class FindAllRelevantNodesVisitor extends ASTVisitor {

    private List<IASTNode> allNodes = new ArrayList<>();

    public FindAllRelevantNodesVisitor() {
        shouldVisitNames = true;
        shouldVisitDeclSpecifiers = true;
        shouldVisitImplicitNames = true;
    }

    @Override
    public int visit(IASTName name) {
        allNodes.add(name);
        return super.visit(name);
    }

    @Override
    public int visit(IASTDeclSpecifier declSpec) {
        allNodes.add(declSpec);
        return super.visit(declSpec);
    }

    public List<IASTNode> getAllRelevantNodes() {
        return allNodes;
    }
}
