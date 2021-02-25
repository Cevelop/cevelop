/******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Ueli Kunz <kunz@ideadapt.net>, Jules Weder <julesweder@gmail.com> - initial API and implementation
 ******************************************************************************/
package com.cevelop.namespactor.tests.tests.sandbox;

/*******************************************************************************
 * Copyright (c) 2012 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Thomas Corbat (IFS) - initial API and implementation
 *******************************************************************************/

import java.io.IOException;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.parser.tests.rewrite.changegenerator.ChangeGeneratorTest;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTModification;

import junit.framework.Test;


@SuppressWarnings("restriction")
public class LastExpressionStatement extends ChangeGeneratorTest {

    private final String source         = "void f()\r\n{\r\n\tint i = 0;\r\n\tf();\r\n}\r\n";
    private final String expectedSource = "void f()\r\n{\r\n\tint i = 0;\r\n\tusing namespace std;\r\n}\r\n";

    public LastExpressionStatement() {
        super("Last Replace After Insert Has No Line Break");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected StringBuilder[] getTestSource(int sections) throws IOException {
        StringBuilder[] sources = new StringBuilder[2];
        sources[0] = new StringBuilder(source);
        sources[1] = new StringBuilder(expectedSource);
        return sources;
    }

    public static Test suite() {
        return new LastExpressionStatement();
    }

    public void testLastReplaceAfterInsertHasNoLineBreak() throws Exception {
        compareResult(new ASTVisitor() {

            {
                shouldVisitStatements = true;
            }

            @Override
            public int visit(IASTStatement statement) {
                if (statement instanceof IASTExpressionStatement) {
                    addModification(null, ASTModification.ModificationKind.REPLACE, statement, null);
                    IASTDeclarationStatement declStatement = factory.newDeclarationStatement(factory.newUsingDirective(factory.newName("std"
                            .toCharArray())));
                    addModification(null, ASTModification.ModificationKind.INSERT_BEFORE, statement, declStatement);
                }
                return PROCESS_CONTINUE;
            }
        });
    }
}
