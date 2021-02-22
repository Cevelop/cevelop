/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.NodeHelper;
import com.cevelop.includator.helpers.offsetprovider.InsertFwdOffsetProvider;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest22InsertFwdOffsetProviderTestNextNodeTest extends IncludatorTest {

    @Test
    public void runTest() throws Throwable {
        IncludatorFile file = getActiveIncludatorFile();
        IASTTranslationUnit tu = file.getTranslationUnit();
        InsertFwdOffsetProvider offsetProvider = new InsertFwdOffsetProvider(tu, null);

        IASTNode insertAfterNode = offsetProvider.getInsertAfterNode();
        int offset = insertAfterNode.getFileLocation().getNodeOffset() + insertAfterNode.getFileLocation().getNodeLength();
        Assert.assertEquals((Object) adaptExpectedOffset(file.getIFile(), 14), (Object) offset);

        IASTNode insertBeforeNode = NodeHelper.findFollowingNonPreprocessorNodeByPosition(insertAfterNode);
        Assert.assertEquals((Object) adaptExpectedOffset(file.getIFile(), 49), (Object) insertBeforeNode.getFileLocation().getNodeOffset());
    }
}
