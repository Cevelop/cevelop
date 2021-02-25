package com.cevelop.aliextor.tests.tests;

import java.util.Properties;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContext;
import org.junit.Test;

import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.refactoring.ProxyRefactoring;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;


public abstract class BaseTest extends CDTTestingRefactoringTest {

    protected AliExtorRefactoring refactoring;
    private String                newName;
    private boolean               shouldSucceed;

    @Override
    protected Refactoring createRefactoring() {
        refactoring = new ProxyRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile());
        return refactoring;
    }

    @Override
    protected void initAdditionalIncludes() throws Exception {
        stageExternalIncludePathsForBothProjects("includes");
        super.initAdditionalIncludes();
    }

    @Test
    public void runTest() throws Throwable {
        closeWelcomeScreen();
        openTestFileInEditor(getNameOfPrimaryTestFile());
        if (shouldSucceed) {
            runRefactoringAndAssertSuccess();
        } else {
            runRefactoringAndAssertFailure();
        }
    }

    @Override
    protected void configureTest(Properties properties) {
        newName = properties.getProperty("newName", "default");
        shouldSucceed = Boolean.parseBoolean(properties.getProperty("shouldSucceed", "true"));
        super.configureTest(properties);
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        refactoring.setTheUserInput(newName);
    }

}
