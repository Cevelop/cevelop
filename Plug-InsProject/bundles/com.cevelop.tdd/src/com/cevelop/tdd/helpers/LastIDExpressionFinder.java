/*******************************************************************************
 * Copyright (c) 2011, IFS Institute for Software, HSR Rapperswil,
 * Switzerland, http://ifs.hsr.ch
 *
 * Permission to use, copy, and/or distribute this software for any
 * purpose without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *******************************************************************************/
package com.cevelop.tdd.helpers;

import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;


public class LastIDExpressionFinder {

    private IASTIdExpression possibleresult;

    public IASTIdExpression getLastIDExpression(IASTNode selectedNode) {
        findLastIDExpression(selectedNode);
        return possibleresult;
    }

    private void findLastIDExpression(IASTNode selectedNode) {
        for (IASTNode child : selectedNode.getChildren()) {
            if (child instanceof IASTIdExpression) {
                possibleresult = (IASTIdExpression) child;
            }
            findLastIDExpression(child);
        }
    }
}
