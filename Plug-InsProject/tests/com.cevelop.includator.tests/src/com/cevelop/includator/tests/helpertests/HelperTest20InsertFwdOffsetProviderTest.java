/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.helpertests;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.offsetprovider.InsertFwdOffsetProvider;
import com.cevelop.includator.resources.IncludatorFile;
import com.cevelop.includator.tests.base.IncludatorTest;


public class HelperTest20InsertFwdOffsetProviderTest extends IncludatorTest {

    private int expectedOffset;
    private int fallbackIncludeStartOffset;

    @Test
    public void runTest() throws Throwable {
        IncludatorFile file = getActiveIncludatorFile();
        IASTTranslationUnit tu = file.getTranslationUnit();
        InsertFwdOffsetProvider offsetProvider = new InsertFwdOffsetProvider(tu, getFallbackInclude(tu));
        IASTFileLocation fileLocation = offsetProvider.getInsertAfterNode().getFileLocation();
        int offset = fileLocation.getNodeOffset() + fileLocation.getNodeLength();
        Assert.assertEquals((Object) adaptExpectedOffset(file.getIFile(), expectedOffset), (Object) offset);
    }

    private IASTPreprocessorIncludeStatement getFallbackInclude(IASTTranslationUnit tu) throws IOException {
        int adaptedFallbackIncludeStartOffset = adaptExpectedOffset(currentProjectHolder.makeProjectAbsoluteURI(getNameOfPrimaryTestFile()),
                fallbackIncludeStartOffset);
        for (IASTPreprocessorIncludeStatement curInclude : tu.getIncludeDirectives()) {
            if (curInclude.getFileLocation().getNodeOffset() == adaptedFallbackIncludeStartOffset) {
                return curInclude;
            }
        }
        return null;
    }

    @Override
    protected void configureTest(Properties properties) {
        fallbackIncludeStartOffset = Integer.parseInt(properties.getProperty("fallbackIncludeStartOffset", "-1"));
        expectedOffset = Integer.parseInt(properties.getProperty("offset"));
        super.configureTest(properties);
    }
}
