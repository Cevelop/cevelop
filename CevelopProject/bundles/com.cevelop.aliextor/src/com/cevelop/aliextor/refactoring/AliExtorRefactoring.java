package com.cevelop.aliextor.refactoring;

import java.util.ArrayList;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTQualifiedName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.ast.ASTHelper.Type;
import com.cevelop.aliextor.ast.selection.BasicRefactorSelection;
import com.cevelop.aliextor.ast.selection.IRefactorSelection;
import com.cevelop.aliextor.ast.selection.PartialRefactorSelection;
import com.cevelop.aliextor.refactoring.strategy.TemplateAliasRefactoringConcreteStrategy;
import com.cevelop.aliextor.wizard.helper.Pair;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


@SuppressWarnings("restriction")
public abstract class AliExtorRefactoring extends CRefactoring {

    protected ICPPNodeFactory     factory;
    protected IASTTranslationUnit ast;

    protected IRefactorSelection refactorSelection;

    private String  theUserInput;
    private boolean justRefactorSelected;
    private boolean extractFunctionDeclaration;
    private boolean extractFunctionPointer;
    private boolean extractFunctionReference;
    private boolean potentialForTemplateAlias;
    private boolean userWantsTemplateAlias;

    public AliExtorRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    protected void init() throws CoreException {
        factory = CPPNodeFactory.getDefault();
        ast = getAST(getTranslationUnit(), null);
        refactorSelection = getASTNodeBySelection();
    }

    private IRefactorSelection getASTNodeBySelection() {
        int selectOffset = selectedRegion.getOffset();
        int selectLength = selectedRegion.getLength();
        IASTNode enclosingNode = ast.getNodeSelector(null).findEnclosingNode(selectOffset, selectLength);

        if (enclosingNode instanceof CPPASTQualifiedName || enclosingNode instanceof CPPASTName) {
            enclosingNode = enclosingNode.getParent();
        }

        int enclosingNodeOffset = enclosingNode.getNodeLocations()[0].asFileLocation().getNodeOffset();
        int enclosingNodeLength = enclosingNode.getNodeLocations()[0].asFileLocation().getNodeLength();

        boolean differentOffset = enclosingNodeOffset != selectOffset;
        boolean differentLength = enclosingNodeLength != selectLength;
        boolean isDeclSpec = ASTHelper.isType(enclosingNode, Type.ICPPASTDeclSpecifier);
        boolean isSimpleDeclaration = ASTHelper.isType(enclosingNode, Type.IASTSimpleDeclaration);
        boolean isNamedTypeSpec = ASTHelper.isType(enclosingNode, Type.ICPPASTNamedTypeSpecifier);
        boolean isTypeId = ASTHelper.isType(enclosingNode, Type.ICPPASTTypeId);

        // Partial selection for decl specifier or mixed with declaration
        if ((isDeclSpec || isSimpleDeclaration || isNamedTypeSpec || isTypeId) && (differentOffset || differentLength)) {

            String selectedString = ast.getRawSignature().substring(selectOffset, selectOffset + selectLength);

            if (isDeclSpec || isNamedTypeSpec) {
                ICPPASTDeclSpecifier declSpec = (ICPPASTDeclSpecifier) enclosingNode;
                return new PartialRefactorSelection(declSpec, null, selectedString);
            } else if (isSimpleDeclaration) {
                IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) enclosingNode;
                return new PartialRefactorSelection(declaration.getDeclSpecifier(), declaration.getDeclarators()[0], selectedString);
            } else if (isTypeId) {
                ICPPASTTypeId typeId = (ICPPASTTypeId) enclosingNode;
                return new PartialRefactorSelection(typeId.getDeclSpecifier(), typeId.getAbstractDeclarator(), selectedString);
            } else {
                return null;
            }

        } else {
            return new BasicRefactorSelection(enclosingNode);
        }

    }

    public void setJustRefactorSelected(boolean justRefactorSelected) {
        this.justRefactorSelected = justRefactorSelected;
    }

    public void setTheUserInput(String theUserInput) {
        this.theUserInput = theUserInput;
    }

    public ICPPNodeFactory getICPPNodeFactory() {
        return factory;
    }

    public IRefactorSelection getIRefactoringSelection() {
        return refactorSelection;
    }

    public boolean shouldJustRefactorSelected() {
        return justRefactorSelected;
    }

    public String getTheUserInput() {
        return theUserInput;
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }

    public boolean shouldExtractFunctionDeclaration() {
        return extractFunctionDeclaration;
    }

    public void setExtractFunctionDeclaration(boolean extractFunctionDeclaration) {
        this.extractFunctionDeclaration = extractFunctionDeclaration;
    }

    public boolean shouldExtractFunctionPointer() {
        return extractFunctionPointer;
    }

    public void setExtractFunctionPointer(boolean extractFunctionPointer) {
        this.extractFunctionPointer = extractFunctionPointer;
    }

    public boolean shouldExtractFunctionReference() {
        return extractFunctionReference;
    }

    public void setExtractFunctionReference(boolean extractFunctionReference) {
        this.extractFunctionReference = extractFunctionReference;
    }

    public boolean hasPotentialForTemplateAlias() {
        return potentialForTemplateAlias;
    }

    public void setPotentialForTemplateAlias(boolean potentialForTemplateAlias) {
        this.potentialForTemplateAlias = potentialForTemplateAlias;
    }

    public boolean userWantsTemplateAlias() {
        return userWantsTemplateAlias;
    }

    public void setUserWantsTemplateAlias(boolean userWantsTemplateAlias) {
        this.userWantsTemplateAlias = userWantsTemplateAlias;
    }

    public ArrayList<IASTNode> getNames() {
        if (refactorSelection.getStrategy() instanceof TemplateAliasRefactoringConcreteStrategy) {
            return ((TemplateAliasRefactoringConcreteStrategy) refactorSelection.getStrategy()).getNames();
        } else {
            return null;
        }
    }

    public ICPPASTNamedTypeSpecifier getSelectedNodeForTemplateAlias() {
        if (refactorSelection.getStrategy() instanceof TemplateAliasRefactoringConcreteStrategy) {
            return ((TemplateAliasRefactoringConcreteStrategy) refactorSelection.getStrategy()).getSelectedNode();
        } else {
            return null;
        }
    }

    public void setSelectedNamesInRefactoring(ArrayList<Pair<String, Integer>> selectedNames) {
        if (refactorSelection.getStrategy() instanceof TemplateAliasRefactoringConcreteStrategy) {
            ((TemplateAliasRefactoringConcreteStrategy) refactorSelection.getStrategy()).setSelectedNames(selectedNames);
        }
    }

}
