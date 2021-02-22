package com.cevelop.aliextor.tests.tests;

import java.util.Properties;

import org.eclipse.ltk.core.refactoring.RefactoringContext;


public class SimpleRefactoringTest extends BaseTest {

    private boolean justRefactorSelected;

    @Override
    protected void configureTest(Properties properties) {
        super.configureTest(properties);

        justRefactorSelected = Boolean.parseBoolean(properties.getProperty("justRefactorSelected", "false"));
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        super.simulateUserInput(context);

        refactoring.setJustRefactorSelected(justRefactorSelected);
    }

}
