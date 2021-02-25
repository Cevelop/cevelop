package com.cevelop.charwars.quickfixes.cstring.general;

import java.util.function.Function;

import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.asttools.ASTRewriteCache;
import com.cevelop.charwars.asttools.DeclaratorAnalyzer;
import com.cevelop.charwars.constants.ErrorMessages;
import com.cevelop.charwars.constants.ProblemId;
import com.cevelop.charwars.constants.QuickFixLabels;
import com.cevelop.charwars.constants.StdString;
import com.cevelop.charwars.constants.StringType;
import com.cevelop.charwars.quickfixes.BaseQuickFix;
import com.cevelop.charwars.quickfixes.cstring.common.BlockRefactoring;
import com.cevelop.charwars.quickfixes.cstring.common.BlockRefactoringConfiguration;
import com.cevelop.charwars.utils.analyzers.DeclaratorTypeAnalyzer;


public class CStringQuickFix extends BaseQuickFix {

    private static final IBetterFactory FACTORY = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

    @Override
    public String getLabel() {
        final String problemId = getProblemId(currentMarker);
        if (problemId.equals(ProblemId.C_STRING_PROBLEM.getId())) {
            return QuickFixLabels.C_STRING;
        } else if (problemId.equals(ProblemId.C_STRING_ALIAS_PROBLEM.getId())) {
            return QuickFixLabels.C_STRING_ALIAS;
        }
        return "Unknown problem.";
    }

    @Override
    protected String getErrorMessage() {
        return ErrorMessages.C_STRING_QUICK_FIX;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite rewrite, ASTRewriteCache rewriteCache) {
        final IASTDeclarator oldDeclarator = (IASTDeclarator) markedNode;
        final IASTNode block = ASTAnalyzer.getEnclosingBlock(oldDeclarator);
        final boolean isNotNested = (block == oldDeclarator.getTranslationUnit() || block instanceof ICPPASTNamespaceDefinition ||
                                     block instanceof IASTCompositeTypeSpecifier);
        final IASTSimpleDeclaration oldDeclaration = (IASTSimpleDeclaration) oldDeclarator.getParent();
        final IASTDeclarationStatement oldDeclarationStatement = isNotNested ? null : (IASTDeclarationStatement) oldDeclaration.getParent();
        final IASTNode beforeNode = isNotNested ? oldDeclaration : oldDeclarationStatement;

        for (final IASTDeclarator declarator : oldDeclaration.getDeclarators()) {
            insertNewDeclarationStatementFromDeclarator(declarator, beforeNode, declarator.equals(oldDeclarator), block, rewrite);
        }

        ASTModifier.remove(beforeNode, rewrite);

        final IASTName varName = oldDeclarator.getName();
        IASTName strName;
        final boolean isAlias = (getProblemId(currentMarker).equals(ProblemId.C_STRING_ALIAS_PROBLEM.getId()));

        if (isAlias) {
            final IASTInitializerClause initializerClause = DeclaratorAnalyzer.getSingleElementInitializerClause(oldDeclarator.getInitializer());
            final IASTIdExpression idExpression = ASTAnalyzer.getStdStringIdExpression((IASTExpression) initializerClause);
            strName = idExpression.getName();
        } else {
            strName = varName;
        }

        final BlockRefactoringConfiguration config = new BlockRefactoringConfiguration();
        config.setBlock(block);
        config.skipStatement(oldDeclarationStatement);
        config.setASTRewrite(rewrite);
        config.setStringType(StringType.createFromDeclSpecifier(oldDeclaration.getDeclSpecifier()));
        config.setStrName(strName);
        config.setVarName(varName);

        final BlockRefactoring blockRefactoring = new BlockRefactoring(config);
        blockRefactoring.refactorAllStatements();

        headers.addAll(blockRefactoring.getHeadersToInclude());
        headers.add(StdString.HEADER_NAME);
    }

    private int getStringBufferSizeFromDeclarator(IASTDeclarator declarator) {
        int stringBufferSize = -1;
        for (final IASTNode child : declarator.getChildren()) {
            if (child instanceof IASTArrayModifier) {
                final IASTArrayModifier arrayModifier = (IASTArrayModifier) child;
                final IASTExpression constantExpr = arrayModifier.getConstantExpression();
                if (constantExpr != null) {
                    stringBufferSize = Integer.parseInt(constantExpr.getRawSignature());
                    break;
                }
            }
        }
        return stringBufferSize;
    }

    private static IASTInitializer mapSingleElementInitializer(IASTInitializer initializer,
            Function<IASTInitializerClause, IASTInitializerClause> transform) {
        final IASTInitializerClause clause = DeclaratorAnalyzer.getSingleElementInitializerClause(initializer);
        if (clause == null) {
            return null;
        }

        final IASTInitializerClause transformedClause = transform.apply(clause);
        if (transformedClause == null) {
            return null;
        }

        if (initializer instanceof IASTEqualsInitializer) {
            final IASTEqualsInitializer equalsInitializer = (IASTEqualsInitializer) initializer;
            if (equalsInitializer.getInitializerClause() instanceof IASTInitializerList) {
                return FACTORY.newEqualsInitializerWithList(transformedClause);
            } else {
                return FACTORY.newEqualsInitializer(transformedClause);
            }
        } else if (initializer instanceof ICPPASTInitializerList) {
            return FACTORY.newInitializerList(transformedClause);
        } else if (initializer instanceof ICPPASTConstructorInitializer) {
            return FACTORY.newConstructorInitializer(new ICPPASTInitializerClause[] { (ICPPASTInitializerClause) transformedClause });
        }
        return null;
    }

    private IASTDeclarationStatement newRefactoredDeclarationStatementFromDeclarator(IASTDeclarator declarator) {
        if (getProblemId(currentMarker).equals(ProblemId.C_STRING_PROBLEM.getId())) {
            final IASTSimpleDeclSpecifier ds = DeclaratorTypeAnalyzer.getDeclSpecifier(declarator);
            final IASTDeclSpecifier newDeclSpecifier = newRefactoredDeclSpecifier(ds, declarator);
            final IASTSimpleDeclaration newDeclaration = FACTORY.newSimpleDeclaration(newDeclSpecifier);
            final IASTDeclarator newDeclarator = FACTORY.newDeclarator(declarator.getName().toString());

            IASTInitializer initializer;
            if (DeclaratorAnalyzer.hasStrdupAssignment(declarator)) {
                initializer = mapSingleElementInitializer(declarator.getInitializer(), clause -> {
                    if (clause instanceof IASTFunctionCallExpression) {
                        final IASTFunctionCallExpression strdupCall = (IASTFunctionCallExpression) clause;
                        return strdupCall.getArguments()[0].copy();
                    }
                    return null;
                });
            } else {
                initializer = declarator.getInitializer().copy();
            }

            newDeclarator.setInitializer(initializer);
            newDeclaration.addDeclarator(newDeclarator);
            return FACTORY.newDeclarationStatement(newDeclaration);
        } else {
            final String name = declarator.getName().toString();
            final IASTInitializer initializer = mapSingleElementInitializer(declarator.getInitializer(), clause -> {
                if (clause instanceof IASTExpression) {
                    final IASTExpression existingOffset = ASTAnalyzer.extractPointerOffset((IASTExpression) clause);
                    return (existingOffset != null) ? existingOffset : FACTORY.newIntegerLiteral(0);
                }
                return null;
            });

            return FACTORY.newDeclarationStatement(StdString.STRING_SIZE_TYPE, name, initializer);
        }
    }

    private IASTDeclSpecifier newRefactoredDeclSpecifier(IASTSimpleDeclSpecifier oldDeclSpecifier, IASTDeclarator oldDeclarator) {
        final IASTDeclSpecifier newDeclSpecifier = FACTORY.newNamedTypeSpecifier(DeclaratorAnalyzer.getStringReplacementType(oldDeclarator));
        newDeclSpecifier.setStorageClass(oldDeclSpecifier.getStorageClass());
        final IASTPointerOperator pointerOperators[] = oldDeclarator.getPointerOperators();
        if (pointerOperators.length > 0) {
            final IASTPointer pointer = (IASTPointer) pointerOperators[0];
            newDeclSpecifier.setConst(pointer.isConst() && oldDeclSpecifier.isConst());
        }
        newDeclSpecifier.setVolatile(oldDeclSpecifier.isVolatile());
        return newDeclSpecifier;
    }

    private void insertNewDeclarationStatementFromDeclarator(IASTDeclarator declarator, IASTNode beforeNode, boolean isOldDeclarator, IASTNode block,
            ASTRewrite rewrite) {
        final boolean isGlobal = (block == declarator.getTranslationUnit());

        IASTNode nodeToInsert = isOldDeclarator ? newRefactoredDeclarationStatementFromDeclarator(declarator) : FACTORY
                .newDeclarationStatementFromDeclarator(declarator);
        if (isGlobal) {
            nodeToInsert = ((IASTDeclarationStatement) nodeToInsert).getDeclaration();
        }

        ASTModifier.insertBefore(block, beforeNode, nodeToInsert, rewrite);

        if (isOldDeclarator && !isGlobal) {
            final int stringBufferSize = getStringBufferSizeFromDeclarator(declarator);
            if (stringBufferSize != -1) {
                final IASTLiteralExpression new_cap = FACTORY.newIntegerLiteral(stringBufferSize);
                final IASTFunctionCallExpression reserveCall = FACTORY.newMemberFunctionCallExpression(declarator.getName(), StdString.RESERVE,
                        new_cap);
                final IASTExpressionStatement reserveCallStatement = FACTORY.newExpressionStatement(reserveCall);
                ASTModifier.insertBefore(block, beforeNode, reserveCallStatement, rewrite);
            }
        }
    }
}
