package com.cevelop.includator.helpers.offsetprovider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.includator.helpers.NodeHelper;


public class InsertFwdOffsetProvider {

    private IASTNode           resultNode;
    private IASTNode           lastSuboptimalInclude;
    private IASTNode           usefullEndifNode;
    private IASTNode           includeGuardDefineNode;
    private Collection<String> potentialIncludeGuardNames;

    /**
     * Finds the node after which a forward declaration should be inserted. if no optimal location can be evaluated, the fallbackInclude will be
     * returned.
     *
     * @param tu
     * Translation unit to find node to insert after
     * @param fallbackInclude A fallback include preprocessor statement to be used
     */
    public InsertFwdOffsetProvider(IASTTranslationUnit tu, IASTPreprocessorIncludeStatement fallbackInclude) {
        potentialIncludeGuardNames = new ArrayList<>();
        calulateNodeToInsertAfter(tu, fallbackInclude);
    }

    public IASTNode getInsertAfterNode() {
        return resultNode;
    }

    private void calulateNodeToInsertAfter(IASTTranslationUnit tu, IASTPreprocessorIncludeStatement fallbackInclude) {
        calculateExistingTopLevelFwdNode(tu);
        if (resultNode == null) {
            calculateExistingInclude(tu);
        }
        if (resultNode == null) {
            calculateSuboptimalNode();
        }
        if (resultNode == null) {
            resultNode = fallbackInclude;
        }
    }

    private void calculateExistingTopLevelFwdNode(IASTTranslationUnit tu) {
        for (IASTDeclaration curDecl : tu.getDeclarations()) {
            if (NodeHelper.isForwardDeclaration(curDecl)) {
                resultNode = curDecl;
            } else {
                break; // prevent insertion of fwd after any none-fwd-declaration
            }
        }
    }

    private void calculateExistingInclude(IASTTranslationUnit tu) {
        int openIfCount = 0;
        int fistNonFwdDeclOffset = getFirstNonFwdDeclarationOffset(tu);

        for (IASTNode curStatement : tu.getAllPreprocessorStatements()) {
            if (curStatement.getFileLocation().getNodeOffset() > fistNonFwdDeclOffset) {
                break; // never insert after a declaration
            }
            if (isConditionalIfOpen(curStatement)) {
                openIfCount += 1;
            } else if ((openIfCount > 0) && (isConditionalIfClose(curStatement) || isDefineStatemtentOfIncludeGuard(curStatement))) {
                openIfCount -= 1;
                if (isConditionalIfClose(curStatement) && (openIfCount == 0) && (lastSuboptimalInclude != null)) {
                    usefullEndifNode = curStatement;
                    lastSuboptimalInclude = null;
                }
            } else if (isInclude(curStatement)) {
                if (openIfCount == 0) {
                    resultNode = curStatement;
                } else {
                    lastSuboptimalInclude = curStatement;
                }
            }
        }
    }

    private boolean isInclude(IASTNode statement) {
        return statement instanceof IASTPreprocessorIncludeStatement;
    }

    private boolean isDefineStatemtentOfIncludeGuard(IASTNode statement) {
        if (!(statement instanceof IASTPreprocessorObjectStyleMacroDefinition)) {
            return false;
        }
        String defineName = ((IASTPreprocessorObjectStyleMacroDefinition) statement).getName().toString();
        if (potentialIncludeGuardNames.contains(defineName)) {
            includeGuardDefineNode = statement;
            return true;
        }
        return false;
    }

    private boolean isConditionalIfClose(IASTNode statement) {
        return statement instanceof IASTPreprocessorEndifStatement;
    }

    private boolean isConditionalIfOpen(IASTNode statement) {
        if (statement instanceof IASTPreprocessorIfndefStatement) {
            potentialIncludeGuardNames.add(((IASTPreprocessorIfndefStatement) statement).getMacroReference().toString());
            return true;
        }
        return (statement instanceof IASTPreprocessorIfdefStatement) || (statement instanceof IASTPreprocessorIfStatement);
    }

    private void calculateSuboptimalNode() {
        if (usefullEndifNode != null) {
            resultNode = usefullEndifNode;
        } else if (includeGuardDefineNode != null) {
            resultNode = includeGuardDefineNode;
        } else {
            resultNode = null; // happens if there is no include statement at all
        }
    }

    private int getFirstNonFwdDeclarationOffset(IASTTranslationUnit tu) {
        for (IASTDeclaration curDecl : tu.getDeclarations()) {
            if (NodeHelper.isForwardDeclaration(curDecl)) {
                continue;
            }
            return curDecl.getFileLocation().getNodeOffset();
        }
        return Integer.MAX_VALUE;
    }
}
