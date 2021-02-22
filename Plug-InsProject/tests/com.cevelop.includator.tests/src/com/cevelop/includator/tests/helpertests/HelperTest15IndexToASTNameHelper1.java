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


public class HelperTest15IndexToASTNameHelper1 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 5, (Object) refs.size());

        DeclarationReference xClassRef = refs.get(2);
        DeclarationReferenceDependency xClassDefinitionDependency = getRequiredDependency(xClassRef);
        Declaration xClassDefinition = xClassDefinitionDependency.getDeclaration();
        IASTTranslationUnit targetTu = xClassDefinition.getFile().getTranslationUnit();
        IIndex index = getActiveProject().getIndex();

        List<IASTName> allMatches = IndexToASTNameHelper.findNamesIn(targetTu, xClassRef.getBinding(), index);
        Assert.assertEquals((Object) 2, (Object) allMatches.size());
        assertName("X.h", "X", 6, allMatches.get(0));
        assertName("X.h", "X", 16, allMatches.get(1));

        IASTName match = IndexToASTNameHelper.findMatchingASTName(targetTu, xClassDefinition.getName(), index);
        assertName("X.h", "X", 16, match);
    }
}
