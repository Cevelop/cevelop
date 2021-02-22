package com.cevelop.templator.plugin.asttools.data;

import org.eclipse.cdt.core.dom.ast.IASTNode;


public class ResolvedName {

    private IASTNode                 originalNode;
    private AbstractResolvedNameInfo info;

    public ResolvedName(IASTNode originalNode, AbstractResolvedNameInfo info) {
        this.originalNode = originalNode;
        this.info = info;
    }

    public IASTNode getOriginalNode() {
        return originalNode;
    }

    public AbstractResolvedNameInfo getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "FoundName [originalName=" + originalNode + ", info=" + info + "]";
    }
}
