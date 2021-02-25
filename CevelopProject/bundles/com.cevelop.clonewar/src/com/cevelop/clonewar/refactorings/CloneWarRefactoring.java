package com.cevelop.clonewar.refactorings;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;

import com.cevelop.clonewar.transformation.ETTPFunctionTransform;
import com.cevelop.clonewar.transformation.ETTPTypeTransform;
import com.cevelop.clonewar.transformation.Transform;


/**
 * Entry point for the clonewar refactoring plug-in. Based on the selected AST
 * Node the appropriate transformation is chosen and applied.
 *
 * @author ythrier(at)hsr.ch
 * @author tcorbat(at)hsr.ch
 */

public class CloneWarRefactoring extends CRefactoring {

    private Transform transformation;

    /**
     * {@inheritDoc}
     */
    public CloneWarRefactoring(ICElement element, Optional<ITextSelection> selection) {
        super(element, selection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        RefactoringStatus status = super.checkInitialConditions(pm);
        determineRefactoringType(status);
        if (status.hasError()) {
            return status;
        }
        transformation.preprocess(status);
        return status;
    }

    /**
     * Returns the transformation of this refactoring.
     *
     * @return Transformation.
     */
    public Transform getTransformation() {
        return transformation;
    }

    /**
     * Try to find an appropriate refactoring based on the user selection (AST
     * Node).
     *
     * @param status
     * A success refactoring status if an appropriate refactoring was
     * found, otherwise error.
     * @return
     */
    private void determineRefactoringType(RefactoringStatus status) {
        final RefactoringResolver resolver = new RefactoringResolver(selectedRegion);
        IASTTranslationUnit translationUnit;
        try {
            translationUnit = getAST(tu, new NullProgressMonitor());
            translationUnit.accept(resolver);
            if (resolver.foundRefactoring()) {
                transformation = resolver.getRefactoring();
                transformation.setTranslationUnit(translationUnit);
            } else {
                status.addFatalError("No type/function selected!");
            }
        } catch (OperationCanceledException e) {
            status.addFatalError("No type/function selected!");
        } catch (CoreException e) {
            status.addFatalError("No type/function selected!");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        transformation.performChanges(collector);
    }

    /**
     * Helper class deciding which refactoring to apply, based on the selected
     * node.
     *
     * @author ythrier(at)hsr.ch
     */
    private class RefactoringResolver extends ASTVisitor {

        private final Region region;
        private Transform    refactoring;

        /**
         * Create the refactoring resolver.
         *
         * @param region
         * Region to search.
         */
        public RefactoringResolver(Region region) {
            this.shouldVisitDeclarations = true;
            this.shouldVisitDeclSpecifiers = true;
            this.region = region;
        }

        /**
         * Returns true if an appropriate refactoring was found for the
         * currently selected node.
         *
         * @return True if a refactoring can be performed for the selected node,
         * otherwise false.
         */
        public boolean foundRefactoring() {
            return refactoring != null;
        }

        /**
         * Returns the refactoring the can be performed based on the selected
         * AST node found in the translation unit.
         *
         * @return Refactoring that can be performed.
         */
        public Transform getRefactoring() {
            return refactoring;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int leave(IASTDeclaration declaration) {
            if (isSelectedNode(declaration)) {
                if (isFunction(declaration)) {
                    refactoring = new ETTPFunctionTransform(refactoringContext);
                    refactoring.setNode(declaration);
                    return PROCESS_ABORT;
                }
                ICPPASTCompositeTypeSpecifier type = findTypeDef(declaration);
                if ((type != null) && isType(type)) {
                    refactoring = new ETTPTypeTransform();
                    refactoring.setNode(type.getParent());
                    refactoring.setSingleSelection(((CPPASTSimpleDeclaration) declaration).getDeclSpecifier());
                    return PROCESS_ABORT;
                }
            }
            return PROCESS_CONTINUE;
        }

        /**
         * Find the definition of a type (struct/class).
         *
         * @param declaration
         * Declaration.
         * @return Type definition.
         */
        private CPPASTCompositeTypeSpecifier findTypeDef(IASTDeclaration declaration) {
            IASTNode node = declaration;
            while ((node != null) && !(node instanceof ICPPASTCompositeTypeSpecifier)) {
                node = node.getParent();
            }
            return (CPPASTCompositeTypeSpecifier) node;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int leave(IASTDeclSpecifier declSpec) {
            if (isSelectedNode(declSpec)) {
                int status = PROCESS_CONTINUE;
                IASTDeclaration decl = findFunctionDef(declSpec);
                if (decl != null && isFunction(decl)) {
                    status = leave(findFunctionDef(declSpec));
                    refactoring.setSingleSelection(declSpec);
                } else if (isType(declSpec)) {
                    refactoring = new ETTPTypeTransform();
                    refactoring.setNode(declSpec.getParent());
                    status = PROCESS_ABORT;
                }
                return status;
            }
            return PROCESS_CONTINUE;
        }

        /**
         * Check if a given selection is a type (struct/class).
         *
         * @param node
         * Node.
         * @return True if the node is a type, otherwise false.
         */
        private boolean isType(IASTNode node) {
            return (node instanceof CPPASTCompositeTypeSpecifier);
        }

        /**
         * Walk up the selection path until the function node was found.
         *
         * @param declSpec
         * Declaration specifier.
         * @return Process abort flag if the the type is
         */
        private IASTDeclaration findFunctionDef(IASTDeclSpecifier declSpec) {
            IASTNode node = declSpec;
            while ((node != null) && !(node instanceof ICPPASTFunctionDefinition)) {
                node = node.getParent();
            }
            return (IASTDeclaration) node;
        }

        /**
         * Check if the node is a function.
         *
         * @param node
         * Node.
         * @return True if the node is a function, otherwise false.
         */
        private boolean isFunction(IASTNode node) {
            return (node instanceof ICPPASTFunctionDefinition);
        }

        /**
         * Check if the passed node is selected by the user.
         *
         * @param node
         * Node to check.
         * @return True if this node is selected, otherwise false.
         */
        private boolean isSelectedNode(IASTNode node) {
            return SelectionHelper.doesNodeOverlapWithRegion(node, region);
        }
    }

    @Override
    protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor, CheckConditionsContext checkContext) throws CoreException,
            OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        transformation.postprocess(status);
        return status;
    }
}
