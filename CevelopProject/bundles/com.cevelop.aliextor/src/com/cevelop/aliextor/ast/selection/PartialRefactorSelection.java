package com.cevelop.aliextor.ast.selection;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.parser.IToken;

import com.cevelop.aliextor.ast.ASTHelper;
import com.cevelop.aliextor.refactoring.AliExtorRefactoring;
import com.cevelop.aliextor.refactoring.strategy.PartialDeclSpecRefactoringConctreteStrategy;
import com.cevelop.aliextor.refactoring.strategy.RefactoringStrategy;


public class PartialRefactorSelection implements IRefactorSelection {

    public static class TQ_SC_PO {

        public boolean                        isConst;
        public boolean                        isVolatile;
        public int                            storageClass;
        public ArrayList<IASTPointerOperator> pointerOperators;
    }

    private TQ_SC_PO            tqOfNamedTypeSpec;
    private TQ_SC_PO            tqOfAliasDecl;
    private ArrayList<String>   fatalErrors;
    private IASTDeclSpecifier   declSpec;
    private IASTDeclarator      declarator;
    private String              selectionString;
    private RefactoringStrategy strategy;

    public PartialRefactorSelection(IASTDeclSpecifier declSpec, IASTDeclarator declarator, String selectionString) {

        IASTPointerOperator[] pointerOperators = null;
        this.declarator = declarator;

        if (declarator != null) {
            pointerOperators = declarator.getPointerOperators();
        }

        this.selectionString = selectionString;

        this.declSpec = declSpec;

        fatalErrors = new ArrayList<>();
        tqOfNamedTypeSpec = loadTq(declSpec);
        tqOfAliasDecl = new TQ_SC_PO();
        tqOfAliasDecl.pointerOperators = new ArrayList<>();

        if (!selectionString.contains(ASTHelper.getDeclSpecWithoutTypeQualifiersAndStorageClass((ICPPASTDeclSpecifier) declSpec).toString())) {
            fatalErrors.add("Typename not found in selection");
        }

        IToken token = null;
        try {
            token = declSpec.getSyntax();
        } catch (ExpansionOverlapsBoundaryException e) {
            fatalErrors.add("Internal error: " + e.getMessage());
        }

        String[] splited = selectionString.split("\\s+");

        prepareReplacements(token, splited);

        if (pointerOperators != null) {

            String[] temp = selectionString.split(">");

            selectionString = temp[temp.length - 1];

            long numOfPointerOperatorsSelected = selectionString.chars().filter(c -> c == '*' || c == '&').count();
            int count = selectionString.length() - selectionString.replace("&&", " ").length();

            numOfPointerOperatorsSelected -= count;

            preparePointerOperators(pointerOperators, numOfPointerOperatorsSelected);
        }

    }

    private void preparePointerOperators(IASTPointerOperator[] pointerOperators, long numOfPointerOperatorsSelected) {
        int ii = 0;
        for (; ii < numOfPointerOperatorsSelected; ii++) {
            tqOfAliasDecl.pointerOperators.add(pointerOperators[ii].copy(CopyStyle.withLocations));
        }
        for (; ii < pointerOperators.length; ii++) {
            tqOfNamedTypeSpec.pointerOperators.add(pointerOperators[ii].copy(CopyStyle.withLocations));
        }
    }

    private void prepareReplacements(IToken token, String[] splited) {
        int i = 0;
        while (token != null && splited.length > i) {
            String tokenString = token.getImage();
            if (tokenString.equals("*") || splited[i].equals("*") || tokenString.equals("&") || splited[i].equals("&") || tokenString.equals("&&") ||
                splited[i].equals("&&")) {
                break;
            }
            if (tokenString.equals(splited[i])) {
                switch (tokenString) {
                case "volatile":
                    tqOfNamedTypeSpec.isVolatile = false;
                    tqOfAliasDecl.isVolatile = true;
                    break;
                case "const":
                    tqOfNamedTypeSpec.isConst = false;
                    tqOfAliasDecl.isConst = true;
                    break;
                }
                i++;
            }
            token = token.getNext();
        }
    }

    @Override
    public IASTNode getSelectedNode() {
        if (declarator == null) {
            return declSpec;
        } else {
            return declSpec.getParent();
        }
    }

    private TQ_SC_PO loadTq(IASTDeclSpecifier declSpec) {
        TQ_SC_PO tq = new TQ_SC_PO();
        tq.isConst = declSpec.isConst();
        tq.isVolatile = declSpec.isVolatile();
        tq.storageClass = declSpec.getStorageClass();
        tq.pointerOperators = new ArrayList<>();
        return tq;
    }

    public ArrayList<String> getFatalErrors() {
        return fatalErrors;
    }

    public TQ_SC_PO getTqOfAliasDecl() {
        return tqOfAliasDecl;
    }

    public TQ_SC_PO getTqOfNamedTypeSpec() {
        return tqOfNamedTypeSpec;
    }

    public IASTDeclSpecifier getDeclSpec() {
        return declSpec;
    }

    public String getSelectionString() {
        return selectionString;
    }

    public IASTDeclarator getDeclarator() {
        return declarator;
    }

    @Override
    public void setStrategy(AliExtorRefactoring refactoring) {
        strategy = new PartialDeclSpecRefactoringConctreteStrategy(refactoring);
    }

    @Override
    public RefactoringStrategy getStrategy() {
        return strategy;
    }

}
