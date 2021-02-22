package com.cevelop.charwars.quickfixes.array;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.constants.StdArray;
import com.cevelop.charwars.quickfixes.BaseQuickFix;
import com.cevelop.charwars.utils.analyzers.ExpressionAnalyzer;


public class ArrayQuickFix extends BaseQuickFix {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    @Override
    public String getLabel() {
        return QuickFixLabels.ARRAY;
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.ARRAY_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        final IASTDeclarator oldDeclarator = (IASTDeclarator) markedNode;
        final IASTNode block = ASTAnalyzer.getEnclosingBlock(oldDeclarator);

        final IASTSimpleDeclaration oldDeclaration = (IASTSimpleDeclaration) oldDeclarator.getParent();
        final boolean mustInsertInsteadOfReplace = oldDeclaration.getDeclarators().length > 1;

        if (mustInsertInsteadOfReplace) {
            final IASTDeclarationStatement oldDeclarationStatement = getOldDeclarationStatementIfExistent(oldDeclaration);
            IASTNode beforeNode = oldDeclaration;
            final boolean requiresDeclarationStatement = oldDeclarationStatement != null;
            for (final IASTDeclarator declarator : oldDeclaration.getDeclarators()) {
                final IASTSimpleDeclaration simpleDeclaration = newRefactoredSimpleDeclarationFromDeclarator(declarator);
                if (simpleDeclaration == null) {
                    return;
                }
                IASTNode nodeToInsert = simpleDeclaration;
                if (requiresDeclarationStatement) {
                    beforeNode = oldDeclarationStatement;
                    nodeToInsert = FACTORY.newDeclarationStatement(simpleDeclaration);
                }
                ASTModifier.insertBefore(block, beforeNode, nodeToInsert, rewrite);
            }
            ASTModifier.remove(beforeNode, rewrite);
        } else {
            final IASTDeclarator declarator = oldDeclarator;
            final IASTSimpleDeclaration simpleDeclaration = newRefactoredSimpleDeclarationFromDeclarator(declarator);
            if (simpleDeclaration == null) {
                return;
            }
            ASTModifier.replace(oldDeclaration, simpleDeclaration, rewrite);
        }

        final IASTName oldName = oldDeclarator.getName();
        final ReplaceIdExpressionsVisitor visitor = new ReplaceIdExpressionsVisitor(rewrite, oldName);
        block.accept(visitor);

        headers.add(StdArray.HEADER_NAME);
    }

    private IASTDeclarationStatement getOldDeclarationStatementIfExistent(IASTSimpleDeclaration oldDeclaration) {
        final IASTNode parent = oldDeclaration.getParent();
        return parent instanceof IASTDeclarationStatement ? (IASTDeclarationStatement) parent : null;
    }

    private IASTExpression getArrayCountExpression(IASTArrayModifier modifier, IASTDeclarator declarator) {
        final IASTExpression constantExpression = modifier.getConstantExpression(); // assumes to be copied with location info
        if (ExpressionAnalyzer.isConstexprExpression(constantExpression)) {
            return constantExpression;
        } else {
            final IASTInitializerList initializerList = getArrayInitializerClause(declarator);
            if (initializerList != null) {
                return FACTORY.newIntegerLiteral(initializerList.getClauses().length);
            }
        }
        return null;
    }

    private IASTSimpleDeclaration newRefactoredSimpleDeclarationFromDeclarator(IASTDeclarator declarator) {
        final IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) declarator.getParent();
        final boolean isArrayDeclarator = declarator instanceof IASTArrayDeclarator;
        final IASTDeclarator declaratorCopy = declarator.copy(CopyStyle.withLocations);
        declaratorCopy.setParent(declarator.getParent());
        final IASTDeclSpecifier declSpecCopy = declaration.getDeclSpecifier().copy(CopyStyle.withLocations);
        final int storageclass = declSpecCopy.getStorageClass();
        declSpecCopy.setStorageClass(IASTDeclSpecifier.sc_unspecified);

        IASTSimpleDeclaration newSimpleDeclaration;
        IASTDeclarator newDeclarator;
        if (isArrayDeclarator) {
            final IASTArrayDeclarator arrayDeclarator = (IASTArrayDeclarator) declaratorCopy;
            IASTDeclarator nested = arrayDeclarator.getNestedDeclarator();
            if (nested == null) {
                nested = FACTORY.newDeclarator(FACTORY.newName());
            }
            final IASTPointerOperator[] pointerOperators = arrayDeclarator.getPointerOperators();
            for (final IASTPointerOperator operator : pointerOperators) {
                if (operator != null) {
                    nested.addPointerOperator(operator);
                }
            }
            final IASTArrayModifier[] modifiers = arrayDeclarator.getArrayModifiers();
            ICPPASTNamedTypeSpecifier newNamedTypeSpecifier = null;
            IASTTypeId newTypeId = FACTORY.newTypeId(declSpecCopy, nested);
            for (int i = modifiers.length - 1; i >= 0; --i) {
                final IASTArrayModifier modifier = modifiers[i];
                final IASTExpression dimension = getArrayCountExpression(modifier, arrayDeclarator);
                if (dimension == null) {
                    return null;
                }

                newNamedTypeSpecifier = makeStdArray(newTypeId, dimension);
                if (i > 0) {
                    newTypeId = FACTORY.newIASTTypeId(newNamedTypeSpecifier);
                }
            }
            newSimpleDeclaration = FACTORY.newSimpleDeclaration(newNamedTypeSpecifier);
            newDeclarator = FACTORY.newDeclarator(arrayDeclarator.getName().toString());

            final IASTInitializer newInitializer = adaptInitializer(declarator);
            if (newInitializer != null) {
                newDeclarator.setInitializer(newInitializer);
            }
        } else {
            // no array declarator, just use the copy
            newSimpleDeclaration = FACTORY.newSimpleDeclaration(declSpecCopy);
            newDeclarator = declaratorCopy;
        }

        newSimpleDeclaration.addDeclarator(newDeclarator);
        newSimpleDeclaration.getDeclSpecifier().setStorageClass(storageclass); // re-establish original storage class
        return newSimpleDeclaration;
    }

    private IASTInitializer adaptInitializer(IASTDeclarator declarator) {
        final IASTInitializerClause initializerClause = getArrayInitializerClause(declarator);
        if (initializerClause == null) {
            return null;
        }

        final IASTInitializerClause initializerClauseCopy = initializerClause.copy(CopyStyle.withLocations);
        if (declarator.getInitializer() instanceof IASTInitializerList) {
            return FACTORY.newInitializerList(initializerClauseCopy);
        } else {
            return FACTORY.newEqualsInitializerWithList(initializerClauseCopy);
        }
    }

    private ICPPASTNamedTypeSpecifier makeStdArray(IASTTypeId newTypeId, IASTExpression dimension) {
        final ICPPASTName std = FACTORY.newName(StdArray.STD);
        final ICPPASTName array = FACTORY.newName(StdArray.ARRAY);
        final ICPPASTTemplateId templateId = FACTORY.newTemplateId(array);
        templateId.addTemplateArgument(newTypeId);
        templateId.addTemplateArgument(dimension);
        final ICPPASTQualifiedName qualifiedName = FACTORY.newQualifiedName(std);
        qualifiedName.addName(templateId);
        return FACTORY.newTypedefNameSpecifier(qualifiedName);
    }

    private static IASTInitializerList getArrayInitializerClause(IASTDeclarator declarator) {
        final IASTInitializer initializer = declarator.getInitializer();
        if (initializer instanceof IASTEqualsInitializer) {
            final IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            final IASTInitializerClause initializerClause = equalsInitializer.getInitializerClause();
            return initializerClause instanceof IASTInitializerList ? (IASTInitializerList) initializerClause : null;
        } else if (initializer instanceof IASTInitializerList) {
            return (IASTInitializerList) initializer;
        }
        return null;
    }
}
