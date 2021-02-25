package com.cevelop.aliextor.wizard;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;


public class Wizard extends RefactoringWizard {

    private String WIZARD_NAME = "Extract Alias";

    public Wizard(Refactoring refactoring, int flags) {
        super(refactoring, flags);
        setWindowTitle(WIZARD_NAME);
    }

    @Override
    protected void addUserInputPages() {
        IASTNode selectedNode = ((AliExtorRefactoring) getRefactoring()).getIRefactoringSelection().getSelectedNode();
        if (ASTHelper.isType(selectedNode, Type.ICPPASTFunctionDefinition) || ASTHelper.isType(selectedNode, Type.ICPPASTParameterDeclaration)) {
            addPage(new FunctionRefactoringWizardPage(WIZARD_NAME));
        } else {
            addPage(new SimpleRefactoringWizardPage(WIZARD_NAME));
        }
    }

}
