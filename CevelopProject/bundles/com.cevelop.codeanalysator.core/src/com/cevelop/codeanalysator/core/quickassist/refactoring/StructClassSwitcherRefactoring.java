package com.cevelop.codeanalysator.core.quickassist.refactoring;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.internal.core.model.TranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.codeanalysator.core.quickassist.rewrite.QuickAssistRewriteStore;
import com.cevelop.codeanalysator.core.quickassist.runnable.StructClassSwitcherRunnable;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;
import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.IBetterFactory;


@SuppressWarnings("restriction")
public class StructClassSwitcherRefactoring extends RefactoringBase {

    private final ITextSelection          selection;
    private ICPPASTCompositeTypeSpecifier compositeTypeSpecifier;

    public StructClassSwitcherRefactoring(ICElement element, Optional<ITextSelection> selection) {
        this(element, selection, null);
    }

    public StructClassSwitcherRefactoring(ICElement element, Optional<ITextSelection> selection, ICPPASTCompositeTypeSpecifier node) {
        super(element, selection);
        this.selection = selection.get();
        this.compositeTypeSpecifier = node;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (compositeTypeSpecifier == null) {
            ILanguage lang = (tu instanceof TranslationUnit) ? ((TranslationUnit) tu).getLanguageOfContext() : tu.getLanguage();;

            StructClassSwitcherRunnable runnable = new StructClassSwitcherRunnable(selection.getOffset(), selection.getLength());
            IStatus status = runnable.runOnAST(lang, tu.getAST());
            if (status.isOK()) {
                compositeTypeSpecifier = runnable.getCompositeTypeSpecifier();
            } else {
                initStatus.addFatalError("Must select a struct or a class.");
            }
        }
        return initStatus;
    }

    @Override
    protected void collectModifications(QuickAssistRewriteStore store) {
        if (compositeTypeSpecifier != null) {
            ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier = compositeTypeSpecifier.copy();
            store.addReplaceChange(compositeTypeSpecifier, switchedCompositeTypeSpecifier);
            if (compositeTypeSpecifier.getKey() == ICPPASTCompositeTypeSpecifier.k_struct) {
                switchedCompositeTypeSpecifier.setKey(ICPPASTCompositeTypeSpecifier.k_class);
                switchBaseSpecifierVisibilities(switchedCompositeTypeSpecifier, ICPPASTBaseSpecifier.v_public, ICPPASTBaseSpecifier.v_private);
                switchPrologVisibilityLabel(switchedCompositeTypeSpecifier, ICPPASTVisibilityLabel.v_public, ICPPASTVisibilityLabel.v_private, store);
            } else if (compositeTypeSpecifier.getKey() == ICPPASTCompositeTypeSpecifier.k_class) {
                switchedCompositeTypeSpecifier.setKey(ICPPASTCompositeTypeSpecifier.k_struct);
                switchBaseSpecifierVisibilities(switchedCompositeTypeSpecifier, ICPPASTBaseSpecifier.v_private, ICPPASTBaseSpecifier.v_public);
                switchPrologVisibilityLabel(switchedCompositeTypeSpecifier, ICPPASTVisibilityLabel.v_private, ICPPASTVisibilityLabel.v_public, store);
            }
        }
        super.collectModifications(store);
    }

    private void switchPrologVisibilityLabel(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier, int oldDefaultVisibility,
            int newDefaultVisibility, QuickAssistRewriteStore store) {
        addPrologVisibilityLabelIfNone(switchedCompositeTypeSpecifier, oldDefaultVisibility, store);
        removePrologVisibilityLabelIfVisibilityIs(switchedCompositeTypeSpecifier, newDefaultVisibility, store);
    }

    private void addPrologVisibilityLabelIfNone(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier, int visibility,
            QuickAssistRewriteStore store) {
        if (!getPrologVisibilityLabel(switchedCompositeTypeSpecifier).isPresent()) {
            IBetterFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
            ICPPASTVisibilityLabel newPrologVisibilityLabel = factory.newVisibilityLabel(visibility);
            IASTNode insertionPoint = Arrays.stream(switchedCompositeTypeSpecifier.getMembers()) //
                    .findFirst().orElse(null);
            store.addInsertChange(switchedCompositeTypeSpecifier, newPrologVisibilityLabel, insertionPoint);
        }
    }

    private void removePrologVisibilityLabelIfVisibilityIs(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier, int visibility,
            QuickAssistRewriteStore store) {
        getPrologVisibilityLabel(switchedCompositeTypeSpecifier) //
                .filter(prologVisibilityLabel -> prologVisibilityLabel.getVisibility() == visibility) //
                .ifPresent(prologVisibilityLabel -> store.addRemoveChange(prologVisibilityLabel));
    }

    private Optional<ICPPASTVisibilityLabel> getPrologVisibilityLabel(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier) {
        return Arrays.stream(switchedCompositeTypeSpecifier.getMembers()) //
                .findFirst() //
                .filter(ICPPASTVisibilityLabel.class::isInstance) //
                .map(ICPPASTVisibilityLabel.class::cast); //
    }

    private void switchBaseSpecifierVisibilities(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier, int oldDefaultVisibility,
            int newDefaultVisibility) {
        replaceBaseSpecifierVisibilities(switchedCompositeTypeSpecifier, 0, oldDefaultVisibility);
        replaceBaseSpecifierVisibilities(switchedCompositeTypeSpecifier, newDefaultVisibility, 0);
    }

    private void replaceBaseSpecifierVisibilities(ICPPASTCompositeTypeSpecifier switchedCompositeTypeSpecifier, int oldVisibility,
            int newVisibility) {
        Arrays.stream(switchedCompositeTypeSpecifier.getBaseSpecifiers()) //
                .filter(baseSpecifier -> baseSpecifier.getVisibility() == oldVisibility) //
                .forEach(baseSpecifier -> baseSpecifier.setVisibility(newVisibility));
    }
}
