package com.cevelop.aliextor.tests.tests;

import java.util.Properties;

import org.eclipse.ltk.core.refactoring.RefactoringContext;


public class FunctionRefactoringTest extends SimpleRefactoringTest {

    private boolean extractFunctionDeclaration;
    private boolean extractFunctionPointer;
    private boolean extractFunctionReference;

    @Override
    protected void configureTest(Properties properties) {
        super.configureTest(properties);

        extractFunctionDeclaration = Boolean.parseBoolean(properties.getProperty("extractFunctionDeclaration", "false"));
        extractFunctionPointer = Boolean.parseBoolean(properties.getProperty("extractFunctionPointer", "false"));
        extractFunctionReference = Boolean.parseBoolean(properties.getProperty("extractFunctionReference", "false"));
    }

    @Override
    protected void simulateUserInput(RefactoringContext context) {
        super.simulateUserInput(context);

        refactoring.setExtractFunctionDeclaration(extractFunctionDeclaration);
        refactoring.setExtractFunctionPointer(extractFunctionPointer);
        refactoring.setExtractFunctionReference(extractFunctionReference);
    }

}
