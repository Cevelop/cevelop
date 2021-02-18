/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure.constructorrefs;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.tests.base.IncludatorTest;


public class ConstructorRefTest9Pointer extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> references = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) references.size());
        for (DeclarationReference curRef : references) {
            // Tests that no reference refers to a constructorRef
            // in which's case the IASTName of a constructorRef would be an instance of IASTImplicitName
            Assert.assertFalse(curRef.getASTNode() instanceof IASTImplicitName);
        }
    }
}
