package com.cevelop.charwars.quickfixes.cstring.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import com.cevelop.charwars.asttools.ASTAnalyzer;
import com.cevelop.charwars.asttools.ASTModifier;
import com.cevelop.charwars.constants.StringType;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.ASTChangeDescription;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Context;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.Refactoring;
import com.cevelop.charwars.quickfixes.cstring.common.refactorings.RefactoringFactory;
import com.cevelop.charwars.utils.analyzers.BoolAnalyzer;
import com.cevelop.charwars.utils.visitors.StatementsVisitor;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


public class BlockRefactoring {

    private BlockRefactoringConfiguration config;
    private HashSet<String>               headersToInclude;
    private List<Refactoring>             refactorings;

    public BlockRefactoring(BlockRefactoringConfiguration config) {
        this.config = config;
        this.headersToInclude = new HashSet<>();
        this.refactorings = Arrays.asList(RefactoringFactory.createRefactorings());
    }

    public void refactorAllStatements() {
        StatementsVisitor visitor = new StatementsVisitor();
        config.getBlock().accept(visitor);
        IASTStatement allStatements[] = visitor.getStatements();

        Context context = prepareContext(allStatements);

        for (IASTStatement statement : allStatements) {
            if (!config.shouldSkipStatement(statement)) {
                refactorStatement(statement, context);
            }
        }
    }

    private Context prepareContext(IASTStatement[] allStatements) {
        StringType stringType = config.getStringType();
        String strNameString = config.getStrName().toString();
        IASTStatement firstAffectedStatement = findFirstAffectedStatement(allStatements);
        boolean isModified = firstAffectedStatement != null;

        if (config.isAlias()) {
            return Context.newModifiedAliasContext(stringType, strNameString, config.getNewVarNameString());
        } else {
            if (isModified) {
                String newVarNameString = strNameString + "_pos";
                config.setNewVarNameString(newVarNameString);
                IASTLiteralExpression zeroLiteral = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newIntegerLiteral(0);

                IASTDeclarationStatement offsetVarDeclaration = ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newDeclarationStatement(stringType
                        .getSizeType(), newVarNameString, zeroLiteral);
                ASTModifier.insertBefore(firstAffectedStatement.getParent(), firstAffectedStatement, offsetVarDeclaration, config.getASTRewrite());
                return Context.newModifiedStringContext(stringType, strNameString, newVarNameString, firstAffectedStatement);
            } else {
                return Context.newUnmodifiedStringContext(stringType, strNameString);
            }
        }
    }

    private IASTStatement findFirstAffectedStatement(IASTStatement[] allStatements) {
        for (IASTStatement statement : allStatements) {
            if (modifiesCharPointer(statement)) {
                return ASTAnalyzer.getTopLevelParentStatement(statement);
            }
        }
        return null;
    }

    private boolean modifiesCharPointer(IASTStatement statement) {
        List<IASTIdExpression> occurrences = new ArrayList<>();
        collectStringOccurrencesInSubtree(statement, occurrences);

        for (IASTIdExpression occurrence : occurrences) {
            if (ASTAnalyzer.modifiesCharPointer(occurrence)) {
                return true;
            }
        }
        return false;
    }

    private void refactorStatement(IASTStatement statement, Context context) {
        IASTStatement oldStatement = statement;
        IASTStatement newStatement = statement.copy(CopyStyle.withLocations);

        List<IASTIdExpression> stringOccurrences = new ArrayList<>();
        collectStringOccurrencesInSubtree(newStatement, stringOccurrences);
        sortStringOccurrences(stringOccurrences);

        ASTChangeDescription changeDescription = new ASTChangeDescription();
        refactorStringOccurrences(stringOccurrences, changeDescription, context);

        //workaround: copy statement again in order to remove original node locations
        newStatement = newStatement.copy();

        ASTRewrite rewrite = config.getASTRewrite();
        if (changeDescription.shouldRemoveStatement()) {
            rewrite.remove(oldStatement, null);
        } else if (changeDescription.statementHasChanged()) {
            IASTExpression oldCondition = BoolAnalyzer.getCondition(oldStatement);
            IASTExpression newCondition = BoolAnalyzer.getCondition(newStatement);

            if (oldCondition != null) {
                ASTModifier.replace(oldCondition, newCondition, rewrite);
            } else {
                ASTModifier.replace(oldStatement, newStatement, rewrite);
            }
        }
    }

    private void collectStringOccurrencesInSubtree(IASTNode subtree, List<IASTIdExpression> stringOccurrences) {
        if (subtree instanceof IASTIdExpression) {
            IASTIdExpression copiedIdExpression = (IASTIdExpression) subtree;
            IASTIdExpression originalIdExpression = (IASTIdExpression) copiedIdExpression.getOriginalNode();
            if (ASTAnalyzer.isSameName(originalIdExpression.getName(), config.getVarName())) {
                stringOccurrences.add(copiedIdExpression);
            }
        } else {
            for (IASTNode child : subtree.getChildren()) {
                if (!(child instanceof IASTStatement)) {
                    collectStringOccurrencesInSubtree(child, stringOccurrences);
                }
            }
        }
    }

    private void sortStringOccurrences(List<IASTIdExpression> stringOccurrences) {
        Collections.sort(stringOccurrences, (o1, o2) -> getDepth(o2) - getDepth(o1));
    }

    private void refactorStringOccurrences(List<IASTIdExpression> stringOccurrences, ASTChangeDescription changeDescription, Context context) {
        for (IASTIdExpression occurrence : stringOccurrences) {
            refactorStringOccurrence(occurrence, changeDescription, context);
        }
        headersToInclude.addAll(changeDescription.getHeadersToInclude());
    }

    private void refactorStringOccurrence(IASTIdExpression stringOccurrence, ASTChangeDescription changeDescription, Context context) {
        for (Refactoring refactoring : this.refactorings) {
            boolean wasApplied = refactoring.tryToApply(stringOccurrence, context, changeDescription);
            if (wasApplied) {
                return;
            }
        }
    }

    private int getDepth(IASTNode node) {
        int depth = 0;
        for (IASTNode parent = node; parent != null && !(parent instanceof IASTStatement); parent = parent.getParent()) {
            ++depth;
        }
        return depth;
    }

    public HashSet<String> getHeadersToInclude() {
        return headersToInclude;
    }
}
