package com.cevelop.clonewar.transformation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.TextEditGroup;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;
import com.cevelop.clonewar.transformation.configuration.action.ExistingTemplateChangeAction;
import com.cevelop.clonewar.transformation.configuration.action.IConfigChangeAction;
import com.cevelop.clonewar.transformation.configuration.action.NewTemplateChangeAction;
import com.cevelop.clonewar.transformation.configuration.action.SingleSelectionChangeAction;
import com.cevelop.clonewar.transformation.util.ASTTypeVisitor;
import com.cevelop.clonewar.transformation.util.TypeInformation;

import ch.hsr.ifs.iltis.cpp.core.ast.nodefactory.ExtendedNodeFactory;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * Transformation base class to apply different types of transformation based on
 * the selection.
 *
 * @author ythrier(at)hsr.ch
 */

public abstract class Transform {

    private List<IConfigChangeAction> configChanges = new ArrayList<>();
    private TransformConfiguration    configuration;
    private ExtendedNodeFactory       nodeFactory   = ExtendedNodeFactory.getDefault();
    private IASTTranslationUnit       translationUnit;
    protected IASTNode                originalNode;
    private IASTNode                  copyNode;
    private IASTNode                  singleSelection;

    /**
     * Set the transform configuration.
     *
     * @param config
     * Configuration.
     */
    protected void setTransformConfiguration(TransformConfiguration config) {
        this.configuration = config;
    }

    /**
     * Return the configuration.
     *
     * @return Configuration.
     */
    public TransformConfiguration getConfig() {
        return configuration;
    }

    /**
     * Set the translation unit.
     *
     * @param translationUnit
     * Translation unit.
     */
    public void setTranslationUnit(IASTTranslationUnit translationUnit) {
        this.translationUnit = translationUnit;
    }

    /**
     * Set the single selection node.
     *
     * @param singleSelection
     * Single selection node.
     */
    public void setSingleSelection(IASTNode singleSelection) {
        this.singleSelection = singleSelection;
    }

    /**
     * Return the single selection.
     *
     * @return Single selection node.
     */
    protected IASTNode getSingleSelection() {
        return singleSelection;
    }

    /**
     * Check if a single selection was made.
     *
     * @return Single selection.
     */
    protected boolean hasSingleSelection() {
        return singleSelection != null;
    }

    /**
     * Get the translation unit.
     *
     * @return Translation unit.
     */
    protected IASTTranslationUnit getUnit() {
        return translationUnit;
    }

    /**
     * Set the original node and creates a copy.
     *
     * @param originalNode
     * Original node.
     */
    public void setNode(IASTNode originalNode) {
        this.originalNode = originalNode;
        this.copyNode = originalNode.copy();
    }

    /**
     * Returns the original node (frozen).
     *
     * @return Original node.
     */
    protected IASTNode getOriginalNode() {
        return originalNode;
    }

    /**
     * Returns the copy node (not frozen).
     *
     * @return Copy node.
     */
    protected IASTNode getCopyNode() {
        return copyNode;
    }

    /**
     * Create a rewriter for the actual translation unit.
     *
     * @param collector
     * Modification collector.
     * @param translationUnit
     * Translation unit.
     * @return AST rewriter.
     */
    protected ASTRewrite createRewriter(ModificationCollector collector, IASTTranslationUnit translationUnit) {
        return collector.rewriterForTranslationUnit(translationUnit);
    }

    /**
     * Preprocessing. Informations/Errors/Warnings are added to the status.
     *
     * @param status
     * Status collector.
     */
    public void preprocess(RefactoringStatus status) {

        final Map<TypeInformation, List<TransformAction>> actionMap = findTypes(status);

        if (actionMap.isEmpty()) {
            status.addFatalError("No type found to be templated!");
        }

        setTransformConfiguration(new TransformConfiguration(actionMap));
        addConfigChangeActions(configChanges);
        applyConfigChanges(status);
    }

    /**
     * Apply configuration changes.
     *
     * @param status
     * Status.
     */
    private void applyConfigChanges(RefactoringStatus status) {
        for (IConfigChangeAction configChange : configChanges) {
            configChange.applyChange(getConfig(), status);
        }
    }

    /**
     * Find types in AST.
     *
     * @param status
     * Status.
     * @return Type visitor to get type map.
     */
    private Map<TypeInformation, List<TransformAction>> findTypes(RefactoringStatus status) {
        ASTTypeVisitor typeVisitor = new ASTTypeVisitor();
        Map<TypeInformation, List<TransformAction>> actions = typeVisitor.findTypes(originalNode, copyNode);
        if (typeVisitor.hasException()) {
            status.addFatalError(typeVisitor.getException().getMessage());
        }
        return actions;
    }

    /**
     * Preprocessing. Informations/Errors/Warnings are added to the status.
     *
     * @param status
     * Status collector.
     */
    public abstract void postprocess(RefactoringStatus status);

    /**
     * Perform the changes of the transformation.
     *
     * @param collector
     * Modification collector.
     */
    public void performChanges(ModificationCollector collector) {
        applyTypeInfoToActions();
        for (TransformAction action : getConfig().getAllActions()) {
            if (action.shouldPerform()) action.performTransform();
        }
        modificationPostprocessing(collector);
        performTemplateDeclarationProcessing(collector);
    }

    /**
     * Set the type informations on the actions to resolve the template name.
     */
    private void applyTypeInfoToActions() {
        for (TypeInformation type : getConfig().getAllTypes()) {
            for (TransformAction action : getConfig().getActionsOf(type)) {
                action.setTypeInformation(type);
            }
        }
    }

    /**
     * Perform replace operation of old with new definition.
     *
     * @param collector
     * Modification collector.
     * @param oldNode
     * Old node.
     * @param newNode
     * New node.
     */
    private void performReplace(ModificationCollector collector, IASTNode oldNode, IASTNode newNode) {
        ASTRewrite rewriter = createRewriter(collector, translationUnit);
        rewriter.replace(oldNode, newNode, createEditText());
    }

    /**
     * Perform the change or add operation for the template declaration.
     *
     * @param collector
     */
    private void performTemplateDeclarationProcessing(ModificationCollector collector) {
        if (isTemplate())
            performTemplateChange(collector);
        else performTemplateAdd(collector);
    }

    /**
     * Add a template definition.
     *
     * @param collector
     * Modification collector.
     */
    private void performTemplateAdd(ModificationCollector collector) {
        performReplace(collector, getOriginalNode(), createTemplateDeclaration());
    }

    /**
     * Change a template definition.
     *
     * @param collector
     * Modification collector.
     */
    private void performTemplateChange(ModificationCollector collector) {
        performReplace(collector, getOriginalNode().getParent(), createTemplateDeclaration());
    }

    /**
     * Create the template declaration.
     *
     * @return New template node.
     */
    private IASTNode createTemplateDeclaration() {
        IASTDeclaration templateBody = getTemplateBody();
        return getNodeFactory().newTemplateDeclaration(templateBody, createTemplateParams());
    }

    /**
     * Return the template body.
     *
     * @return Template body.
     */
    protected abstract IASTDeclaration getTemplateBody();

    /**
     * Create a template parameter.
     *
     * @param templParams
     * Template parameter list to add the parameter.
     * @param type
     * Type information.
     */
    protected abstract void createTemplateParameter(List<ICPPASTTemplateParameter> templParams, TypeInformation type);

    /**
     * Add config change actions for preprocessing adjustments of the configuration.
     *
     * @param configChanges
     * List to add the config changes.
     */
    protected abstract void addConfigChangeActions(List<IConfigChangeAction> configChanges);

    /**
     * Make post-processing of the change.
     *
     * @param collector
     * Modification collector.
     */
    protected abstract void modificationPostprocessing(ModificationCollector collector);

    /**
     * Create the edit group text of the change.
     *
     * @return Edit group.
     */
    protected abstract TextEditGroup createEditText();

    /**
     * Check if the function to transform is a template function.
     *
     * @return True if the function to change is a template function, otherwise
     * false.
     */
    protected boolean isTemplate() {
        return (getOriginalNode().getParent() instanceof ICPPASTTemplateDeclaration);
    }

    /**
     * Returns the template declaration of the selected node.
     *
     * @return Template declaration.
     */
    protected ICPPASTTemplateDeclaration getTemplateDeclaration() {
        return getTemplateDeclaration(getOriginalNode());
    }

    /**
     * Get the node factory.
     *
     * @return Node factory.
     */
    protected ExtendedNodeFactory getNodeFactory() {
        return nodeFactory;
    }

    /**
     * Return the template declaration of the passed node.
     *
     * @param node
     * Node to get the template declaration from.
     * @return Template declaration.
     */
    private ICPPASTTemplateDeclaration getTemplateDeclaration(IASTNode node) {
        return (ICPPASTTemplateDeclaration) node.getParent();
    }

    /**
     * Add a name change action based on whether the function is a template or not.
     *
     * @param configChanges
     * List to add the action to.
     */
    protected void addNameChangeAction(List<IConfigChangeAction> configChanges) {
        if (isTemplate())
            configChanges.add(new ExistingTemplateChangeAction(getTemplateDeclaration()));
        else configChanges.add(new NewTemplateChangeAction());
    }

    /**
     * Add a single selection change action if a single selection was made.
     *
     * @param configChanges
     * List to add the action to.
     */
    protected void addSingleSelectionChangeAction(List<IConfigChangeAction> configChanges) {
        if (hasSingleSelection()) configChanges.add(new SingleSelectionChangeAction(getOriginalNode(), getCopyNode(), getSingleSelection()));
    }

    /**
     * Create an empty default declarator.
     *
     * @return Declarator.
     */
    protected IASTDeclarator createDefaultDeclarator() {
        IASTDeclarator absDecl = new CPPASTDeclarator(new CPPASTName(new char[] {}));
        return absDecl;
    }

    /**
     * Create the template parameters.
     *
     * @return List of template parameters.
     */
    protected List<ICPPASTTemplateParameter> createTemplateParams() {
        List<ICPPASTTemplateParameter> templParams = new ArrayList<>();
        Set<String> existingParams = new HashSet<>();
        for (TypeInformation type : getConfig().getAllTypesOrdered()) {
            if (!existingParams.contains(type.getTemplateName())) {
                createTemplateParameter(templParams, type);
                existingParams.add(type.getTemplateName());
            }
        }
        return templParams;
    }

    /**
     * Create a template parameter.
     *
     * @param type
     * Type.
     * @return Template parameter.
     */
    protected ICPPASTSimpleTypeTemplateParameter createTemplateParam(TypeInformation type) {
        return getNodeFactory().newTemplateParameterDefinition(type.getTemplateName());
    }

    /**
     * Create a type id with default declarator.
     *
     * @param type
     * Type.
     * @return Type id.
     */
    protected CPPASTTypeId createTypeId(TypeInformation type) {
        if (type.hasDefaultType())
            return new CPPASTTypeId(type.getDefaultType().copy(), createDefaultDeclarator());
        else return new CPPASTTypeId(type.getCallSpecificDefaultType().copy(), createDefaultDeclarator());
    }
}
