/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers.offsetprovider;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;

import com.cevelop.includator.helpers.FileHelper;


@SuppressWarnings("restriction")
public class InsertIncludeOffsetProvider {

    private int                 offset;
    private boolean             isOnNewlineChar;
    private final boolean       findSystemIncludeInsertOffset;
    private PreprocessorScope   preprocessorTree;
    private IncludeGuard        includeGuard;
    private IASTTranslationUnit tu;

    public InsertIncludeOffsetProvider(IASTTranslationUnit tu, boolean findSystemIncludeInsertOffset) {
        this.tu = tu;
        this.findSystemIncludeInsertOffset = findSystemIncludeInsertOffset;
        buildPreprocessorTree();
        determineIncludeGuard();
        determineOffsets();
        cleanup();
    }

    private void cleanup() {
        preprocessorTree = null;
        includeGuard = null;
        tu = null;
    }

    private void determineOffsets() {
        InsertOffsetLocatorVisitor visitor = new InsertOffsetLocatorVisitor(includeGuard, findSystemIncludeInsertOffset, getFirstDeclarationOffset());
        preprocessorTree.accept(visitor);
        offset = visitor.getInsertOffset();
        String code = tu.getRawSignature();
        if (code.length() < offset + FileHelper.NL_LENGTH) {
            isOnNewlineChar = false;
            return;
        }
        isOnNewlineChar = code.substring(offset, offset + FileHelper.NL_LENGTH).equals(FileHelper.NL);
    }

    private void determineIncludeGuard() {
        IncludeGuardVisitor visitor = new IncludeGuardVisitor();
        preprocessorTree.accept(visitor);
        includeGuard = visitor.getIncludeGuard();
        if (includeGuard != null) {
            if (!containsAllDecls(includeGuard, tu) || containsElse(includeGuard)) {
                includeGuard = null;
            }
        }
    }

    private boolean containsElse(IncludeGuard includeGuard) {
        for (BasePreprocessorElement curElement : includeGuard.scope.getContainedPreprocessorStatements()) {
            if (curElement instanceof PreprocessorElement) {
                IASTNode enclosedNode = ((PreprocessorElement) curElement).getEnclosedNode();
                if (enclosedNode instanceof IASTPreprocessorElseStatement || enclosedNode instanceof IASTPreprocessorElifStatement) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsAllDecls(IncludeGuard includeGuard, IASTTranslationUnit tu) {
        IASTPreprocessorStatement[] ppStatements = tu.getAllPreprocessorStatements();
        PreprocessorScope guardScope = includeGuard.scope;
        boolean containsPPStatements = checkContains(ppStatements[0], guardScope) && checkContains(ppStatements[ppStatements.length - 1], guardScope);
        IASTDeclaration[] decls = tu.getDeclarations();
        if (decls.length == 0) {
            return containsPPStatements;
        }
        boolean containsDecls = checkContains(decls[0], guardScope) && checkContains(decls[decls.length - 1], guardScope);
        return containsPPStatements && containsDecls;
    }

    private boolean checkContains(IASTNode node, PreprocessorScope containingScope) {
        int nodeOffset = node.getFileLocation().getNodeOffset();
        return containingScope.getScopeStartNode().getFileLocation().getNodeOffset() <= nodeOffset && containingScope.getScopeEndNode()
                .getFileLocation().getNodeOffset() >= nodeOffset;
    }

    private void buildPreprocessorTree() {
        PreprocessorScope currentScope = new PreprocessorScope();
        for (IASTNode curStatement : tu.getAllPreprocessorStatements()) {
            if (!curStatement.isActive()) { // this is not working because of the
                continue;
            }

            if (isConditionalIfOpen(curStatement)) {
                PreprocessorScope newScope = getNewScope(curStatement, currentScope);
                currentScope.addChild(newScope);
                currentScope = newScope;
            } else if (isConditionalIfClose(curStatement)) {
                currentScope.setScopeEndNode(curStatement);
                PreprocessorScope parent = currentScope.getParent();
                if (parent != null) {
                    currentScope = parent;
                }
            } else {
                currentScope.addChild(getPreprocessorTreeElement(currentScope, curStatement));
            }
        }
        preprocessorTree = currentScope;
    }

    private PreprocessorElement getPreprocessorTreeElement(PreprocessorScope currentScope, IASTNode statement) {
        if (isDefineStatement(statement)) {
            return new PreprocessorDefine((IASTPreprocessorObjectStyleMacroDefinition) statement, currentScope);
        } else if (isIncludeStatement(statement)) {
            return new PreprocessorInclude((IASTPreprocessorIncludeStatement) statement, currentScope);
        } else {
            return new PreprocessorElement(statement, currentScope);
        }
    }

    private PreprocessorScope getNewScope(IASTNode statement, PreprocessorScope currentScope) {
        return new PreprocessorScope(statement, currentScope);
    }

    public int getInsertOffset() {
        return offset;
    }

    public boolean isOffsetOnNewlineChar() {
        return isOnNewlineChar;
    }

    private boolean isIncludeStatement(IASTNode statement) {
        return statement instanceof IASTPreprocessorIncludeStatement;
    }

    private boolean isDefineStatement(IASTNode statement) {
        return statement instanceof IASTPreprocessorObjectStyleMacroDefinition;
    }

    private boolean isConditionalIfClose(IASTNode statement) {
        return statement instanceof IASTPreprocessorEndifStatement;
    }

    private boolean isConditionalIfOpen(IASTNode statement) {
        return ((statement instanceof IASTPreprocessorIfndefStatement) || (statement instanceof IASTPreprocessorIfdefStatement) ||
                (statement instanceof IASTPreprocessorIfStatement));
    }

    private int getFirstDeclarationOffset() {
        if (tu.getDeclarations().length == 0) {
            return Integer.MAX_VALUE;
        }
        return tu.getDeclarations()[0].getFileLocation().getNodeOffset();
    }

    public void adaptOffsetWithCommentMap(NodeCommentMap commentMap, IASTTranslationUnit tu) {
        if (offset == 0) {
            List<IASTComment> comments = commentMap.getFreestandingCommentsForNode(tu);
            if (!comments.isEmpty()) {
                IASTFileLocation fileLocation = comments.get(comments.size() - 1).getFileLocation();
                offset = fileLocation.getNodeOffset() + fileLocation.getNodeLength();
                isOnNewlineChar = true;
            }
        }
    }
}



class IncludeGuardVisitor extends PreprocessorTreeVisitor {

    public PreprocessorInclude lastSeenInclude;
    public IASTNode            usefullEndif;
    public PreprocessorScope   currentScope;
    public IncludeGuard        includeGuard;

    @Override
    public void visit(PreprocessorScope preprocessorScope) {
        currentScope = preprocessorScope;
    }

    @Override
    public void visit(PreprocessorDefine preprocessorDefine) {
        checkIsIncludeGuard(preprocessorDefine);
    }

    private void checkIsIncludeGuard(PreprocessorDefine preprocessorDefine) {
        if (currentScope == null || includeGuard != null) {
            return;
        }
        if (isIfndefScope(currentScope) && matchesName(currentScope, preprocessorDefine) && hasEndNode(currentScope)) {
            includeGuard = new IncludeGuard(currentScope, preprocessorDefine);
        }
    }

    private boolean hasEndNode(PreprocessorScope scope) {
        return scope.getScopeEndNode() != null;
    }

    private boolean matchesName(PreprocessorScope scope, PreprocessorDefine preprocessorDefine) {
        IASTName ifndefName = ((IASTPreprocessorIfndefStatement) scope.getScopeStartNode()).getMacroReference();
        if (ifndefName == null) {
            return false;
        }
        return ifndefName.toString().equals(preprocessorDefine.getName());
    }

    private boolean isIfndefScope(PreprocessorScope scope) {
        return scope.getScopeStartNode() instanceof IASTPreprocessorIfndefStatement;
    }

    @Override
    public void visit(PreprocessorInclude preprocessorInclude) {
        currentScope = null;
    }

    @Override
    public void leave(PreprocessorScope preprocessorScope) {
        currentScope = null;
    }

    public IncludeGuard getIncludeGuard() {
        return includeGuard;
    }
}



class InsertOffsetLocatorVisitor extends PreprocessorTreeVisitor {

    private final boolean findSystemIncludeInsertOffset;
    private IASTNode      activeOffsetNode;
    private boolean       useNextEndif;
    private int           validIndent;
    private final int     firstDeclarationOffset;
    private IASTNode      guardDefineNode;
    private IASTNode      guardStartNode;

    public InsertOffsetLocatorVisitor(IncludeGuard includeGuard, boolean findSystemIncludeInsertOffset, int firstDeclarationOffset) {
        this.findSystemIncludeInsertOffset = findSystemIncludeInsertOffset;
        this.firstDeclarationOffset = firstDeclarationOffset;
        validIndent = 0;
        if (includeGuard != null) {
            guardDefineNode = includeGuard.define.getEnclosedNode();
            guardStartNode = includeGuard.scope.getScopeStartNode();
            validIndent = includeGuard.scope.getIndentLevel();
            setGuardActive(null);
        }
        useNextEndif = false;
    }

    private void setGuardActive(IASTNode follwingNode) {
        IASTFileLocation defineLocation = guardDefineNode.getFileLocation();
        IASTFileLocation startLocation = guardStartNode.getFileLocation();
        setActive(defineLocation.getStartingLineNumber() == startLocation.getStartingLineNumber() + 1 ? guardDefineNode : guardStartNode);
    }

    private void setActive(IASTNode newActiveNode) {
        if (newActiveNode != null) {
            activeOffsetNode = newActiveNode;
        }
    }

    @Override
    public void visit(PreprocessorInclude include) {
        if (shouldIgnoreInclude(include)) {
            return;
        }
        if (validIndent >= include.getParent().getIndentLevel()) {
            if (shouldConsiderIncludeType(include)) {
                setActive(include.getEnclosedNode());
            }
        } else {
            useNextEndif = true;
        }
    }

    private boolean shouldConsiderIncludeType(PreprocessorInclude include) {
        return findSystemIncludeInsertOffset == include.isSystemInclude() || findSystemIncludeInsertOffset;
    }

    private boolean shouldIgnoreInclude(PreprocessorInclude include) {
        return include.getEnclosedNode().getFileLocation().getNodeOffset() > firstDeclarationOffset;
    }

    public int getInsertOffset() {
        if (activeOffsetNode == null) {
            return 0;
        }
        IASTFileLocation location = activeOffsetNode.getFileLocation();
        return location.getNodeOffset() + location.getNodeLength();
    }

    @Override
    public void leave(PreprocessorScope preprocessorScope) {
        if (useNextEndif && validIndent >= preprocessorScope.getParent().getIndentLevel()) {
            useNextEndif = false;
            IASTNode endNode = preprocessorScope.getScopeEndNode();
            if (firstDeclarationOffset > endNode.getFileLocation().getNodeOffset()) {
                setActive(endNode);
            }
        }
    }
}
