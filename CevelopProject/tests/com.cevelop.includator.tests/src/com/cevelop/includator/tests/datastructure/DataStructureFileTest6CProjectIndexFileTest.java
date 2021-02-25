/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.tests.datastructure;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.core.resources.IProjectNature;
import org.junit.Assert;
import org.junit.Test;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.tests.base.IncludatorTest;


public class DataStructureFileTest6CProjectIndexFileTest extends IncludatorTest {

    //   @Override
    //   protected void initCurrentExpectedProjectHolders() throws InterruptedException {
    //      currentProjectHolder = new TestProjectHolder(makeCurrentProjectName(), false);
    //      expectedProjectHolder = new TestProjectHolder(makeExpectedProjectName(), true);
    //      currentProjectHolder.setLanguage();
    //      expectedProjectHolder.setLanguage();
    //      scheduleAndJoinBoth(currentProjectHolder.createProjectAsync(), expectedProjectHolder.createProjectAsync());
    //   } FIXME remove after testing

    @Test
    public void runTest() throws Throwable {
        IProjectNature cCNature = getCurrentProject().getNature(CCProjectNature.CC_NATURE_ID);
        IProjectNature cNature = getCurrentProject().getNature(CProjectNature.C_NATURE_ID);
        Assert.assertNull(cCNature);
        Assert.assertNotNull(cNature);

        IIndexFile aHFile = FileHelper.getIndexFile(getIncludatorFile("A.h"));
        Assert.assertEquals((Object) GCCLanguage.getDefault().getLinkageID(), (Object) aHFile.getLinkageID());
    }
}
