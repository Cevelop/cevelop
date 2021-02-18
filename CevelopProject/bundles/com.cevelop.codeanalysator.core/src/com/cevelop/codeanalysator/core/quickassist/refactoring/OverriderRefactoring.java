package com.cevelop.codeanalysator.core.quickassist.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
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
import com.cevelop.codeanalysator.core.quickassist.runnable.OverrideProposoalRunnable;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ASTNodeFactoryFactory;


@SuppressWarnings("restriction")
public class OverriderRefactoring extends RefactoringBase {

    private IASTNode                  node;
    private ITextSelection            selection;
    private OverrideProposoalRunnable runnable;
    private boolean                   isOverridingFunctionDefinition;

    public OverriderRefactoring(ICElement element, Optional<ITextSelection> selection, OverrideProposoalRunnable runnable) {
        this(element, selection);
        this.runnable = runnable;
    }

    public OverriderRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
        this.selection = selection.get();
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        if (runnable == null) {
            ILanguage lang = (tu instanceof TranslationUnit) ? ((TranslationUnit) tu).getLanguageOfContext() : tu.getLanguage();;
            runnable = new OverrideProposoalRunnable(selection.getOffset(), selection.getLength());
            IStatus status = runnable.runOnAST(lang, tu.getAST());
            if (!status.isOK()) {
                initStatus.addFatalError("Runnable failed");
            }
        }
        node = runnable.node;
        isOverridingFunctionDefinition = runnable.isOverridingFunctionDefinition;
        return initStatus;
    }

    @Override
    protected void collectModifications(QuickAssistRewriteStore store) {
        if (node instanceof ICPPASTFunctionDefinition) {
            ICPPASTFunctionDefinition funcDef = (ICPPASTFunctionDefinition) node;
            IASTFunctionDeclarator decl = funcDef.getDeclarator();
            IASTDeclSpecifier declSpec = funcDef.getDeclSpecifier();

            if (decl instanceof ICPPASTFunctionDeclarator && declSpec instanceof ICPPASTDeclSpecifier) {
                ICPPASTFunctionDeclarator newDecl = createOverridingFunctionDeclarator(decl);
                store.addReplaceChange(funcDef.getDeclarator(), newDecl);
                if (isOverridingFunctionDefinition) {
                    ICPPASTDeclSpecifier newDeclSpec = createNonVirtualDeclSpec(declSpec);
                    store.addReplaceChange(declSpec, newDeclSpec);
                }
            }
        }

        super.collectModifications(store);
    }

    private ICPPASTDeclSpecifier createNonVirtualDeclSpec(IASTDeclSpecifier declSpec) {
        ICPPASTDeclSpecifier newDeclSpec = (ICPPASTDeclSpecifier) declSpec.copy();
        newDeclSpec.setVirtual(false);
        return newDeclSpec;
    }

    private ICPPASTFunctionDeclarator createOverridingFunctionDeclarator(IASTFunctionDeclarator decl) {
        ICPPASTFunctionDeclarator newDecl = (ICPPASTFunctionDeclarator) decl.copy();
        ICPPASTVirtSpecifier[] virtSpecs = new ICPPASTVirtSpecifier[] { ASTNodeFactoryFactory.getDefaultCPPNodeFactory().newVirtSpecifier(
                isOverridingFunctionDefinition ? SpecifierKind.Override : null) };
        newDecl.setVirtSpecifiers(virtSpecs);
        return newDecl;
    }

}
