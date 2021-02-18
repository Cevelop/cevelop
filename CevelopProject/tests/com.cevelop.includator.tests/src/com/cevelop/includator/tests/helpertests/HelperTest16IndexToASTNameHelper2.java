/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.cxxelement.Declaration;
import com.cevelop.includator.cxxelement.DeclarationReference;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.helpers.IndexToASTNameHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest16IndexToASTNameHelper2 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) refs.size());

        DeclarationReference xConstrRef = refs.get(4);
        DeclarationReferenceDependency xClassDefinitionDependency = getRequiredDependency(xConstrRef);
        Declaration xConstrDecl = xClassDefinitionDependency.getDeclaration();
        IASTTranslationUnit targetTu = xConstrDecl.getFile().getTranslationUnit();
        IIndex index = getActiveProject().getIndex();

        List<IASTName> allMatches = IndexToASTNameHelper.findNamesIn(targetTu, xConstrRef.getBinding(), index);
        Assert.assertEquals((Object) 2, (Object) allMatches.size());
        assertName("X.h", "X", 30, allMatches.get(0));
        assertName("X.h", "X", 42, allMatches.get(1));

        IASTName match = IndexToASTNameHelper.findMatchingASTName(targetTu, xConstrDecl.getName(), index);
        assertName("X.h", "X", 30, match);
    }
}
