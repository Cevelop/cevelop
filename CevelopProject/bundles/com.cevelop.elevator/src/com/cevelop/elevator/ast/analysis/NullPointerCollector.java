/******************************************************************************
 * Copyright (c) 2015 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Thomas Corbat - initial API and implementation
 ******************************************************************************/
package com.cevelop.elevator.ast.analysis;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;

import com.cevelop.elevator.ast.analysis.conditions.Condition;
import com.cevelop.elevator.ast.analysis.conditions.IsNullMacroExpression;


/**
 * Collects all NULL macro calls, which can be elevated.
 *
 */
public class NullPointerCollector extends ASTVisitor {

    private final List<IASTExpression> nullMacroCalls;
    private final Condition            isElevationCandidate = new IsNullMacroExpression();

    public NullPointerCollector() {
        nullMacroCalls = new ArrayList<>();
        shouldVisitExpressions = true;
    }

    @Override
    public int visit(final IASTExpression expression) {
        if (expression instanceof IASTLiteralExpression) {
            collectIfElevationCandidate(expression);
        }
        return PROCESS_CONTINUE;
    }

    private void collectIfElevationCandidate(IASTExpression expression) {
        if (isElevationCandidate.satifies(expression)) {
            nullMacroCalls.add(expression);
        }
    }

    public List<IASTExpression> getNullMacroCalls() {
        return nullMacroCalls;
    }
}
