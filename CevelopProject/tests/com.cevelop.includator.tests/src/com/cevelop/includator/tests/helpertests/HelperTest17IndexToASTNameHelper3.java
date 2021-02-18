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


public class HelperTest17IndexToASTNameHelper3 extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        List<DeclarationReference> refs = getActiveFileDeclarationReferences();

        Assert.assertEquals((Object) 2, (Object) refs.size());

        DeclarationReference fooRef = refs.get(1);
        DeclarationReferenceDependency fooDependency = getRequiredDependency(fooRef);
        Declaration fooDecl = fooDependency.getDeclaration();
        IASTTranslationUnit targetTu = fooDecl.getFile().getTranslationUnit();
        IIndex index = getActiveProject().getIndex();

        List<IASTName> allMatches = IndexToASTNameHelper.findNamesIn(targetTu, fooRef.getBinding(), index);
        Assert.assertEquals((Object) 3, (Object) allMatches.size());
        assertName("Foo.h", "foo", 5, allMatches.get(0));
        assertName("Foo.h", "foo", 18, allMatches.get(1));
        assertName("Foo.h", "foo", 31, allMatches.get(2));

        IASTName match = IndexToASTNameHelper.findMatchingASTName(targetTu, fooDecl.getName(), index);
        assertName("Foo.h", "foo", 5, match);
    }
}
