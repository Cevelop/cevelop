package com.cevelop.ctylechecker.checker.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;

import com.cevelop.ctylechecker.ids.IdHelper.ProblemId;


public class RedundantAccessSpecifierChecker extends AbstractClassChecker {

    @Override
    public void processAst(IASTTranslationUnit ast) {
        final List<ICPPASTCompositeTypeSpecifier> classDeclarations = new ArrayList<>();
        ast.accept(new ASTVisitor() {

            {
                shouldVisitDeclSpecifiers = true;
            }

            @Override
            public int visit(IASTDeclSpecifier declSpecifier) {
                if (declSpecifier instanceof ICPPASTCompositeTypeSpecifier) {
                    ICPPASTCompositeTypeSpecifier classDeclaration = (ICPPASTCompositeTypeSpecifier) declSpecifier;
                    int classKeyword = classDeclaration.getKey();
                    if (classKeyword == IASTCompositeTypeSpecifier.k_struct || classKeyword == ICPPASTCompositeTypeSpecifier.k_class) {
                        classDeclarations.add(classDeclaration);
                    }
                }
                return PROCESS_CONTINUE;
            }
        });
        classDeclarations.forEach(this::reportRedundantAccessSpecifiers);
    }

    private static int getKeywordVisibility(int classKeyword) {
        if (classKeyword == IASTCompositeTypeSpecifier.k_struct) {
            return ICPPASTVisibilityLabel.v_public;
        }
        return ICPPASTVisibilityLabel.v_private;
    }

    private void reportRedundantAccessSpecifiers(ICPPASTCompositeTypeSpecifier classDeclaration) {
        int defaultVisibility = getKeywordVisibility(classDeclaration.getKey());
        ICPPASTBaseSpecifier[] baseSpecifiers = classDeclaration.getBaseSpecifiers();
        reportRedundantAccessSpecifiersForBases(baseSpecifiers, defaultVisibility);
        ICPPASTVisibilityLabel[] visibilityLabels = findVisibilityLabels(classDeclaration);
        reportRedundantAccessSpecifiersInClass(visibilityLabels, defaultVisibility);
        reportEmptyVisibilityLabel(classDeclaration);
    }

    private void reportRedundantAccessSpecifiersForBases(ICPPASTBaseSpecifier[] baseSpecifiers, int defaultVisibility) {
        for (ICPPASTBaseSpecifier baseSpecifier : baseSpecifiers) {
            if (baseSpecifier.getVisibility() == defaultVisibility) {
                reportRedundantAccessSpecifier(baseSpecifier);
            }
        }
    }

    private void reportRedundantAccessSpecifier(IASTNode node) {
        reportProblem(ProblemId.REDUNDANT_ACCESS_SPECIFIER, node);
    }

    private void reportRedundantAccessSpecifiersInClass(ICPPASTVisibilityLabel[] visibilityLabels, int defaultVisibility) {
        int currentVisibility = defaultVisibility;
        for (ICPPASTVisibilityLabel label : visibilityLabels) {
            int visibility = label.getVisibility();
            if (visibility == currentVisibility) {
                reportProblem(ProblemId.REDUNDANT_ACCESS_SPECIFIER, label);
            }
            currentVisibility = visibility;
        }
    }

    private static ICPPASTVisibilityLabel[] findVisibilityLabels(ICPPASTCompositeTypeSpecifier classDeclaration) {
        return Arrays.stream(classDeclaration.getDeclarations(false))
                .filter(ICPPASTVisibilityLabel.class::isInstance).map(ICPPASTVisibilityLabel.class::cast)
                .toArray(ICPPASTVisibilityLabel[]::new);
    }

    private void reportEmptyVisibilityLabel(ICPPASTCompositeTypeSpecifier classDeclaration) {
        int previousVisibilityIndex = -1;
        IASTDeclaration[] memberDeclaration = classDeclaration.getMembers();
        for (int index = 0; index < memberDeclaration.length; index++) {
            IASTDeclaration child = memberDeclaration[index];
            if (child instanceof ICPPASTVisibilityLabel) {
                if (previousVisibilityIndex == index - 1) {
                    reportRedundantAccessSpecifier(child);
                }
                previousVisibilityIndex = index;
            }
        }
    }
}
