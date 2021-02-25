package com.cevelop.clonewar.transformation;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.TextEditGroup;

import com.cevelop.clonewar.transformation.configuration.action.IConfigChangeAction;
import com.cevelop.clonewar.transformation.configuration.action.TypeOrderingChangeAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;

import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * Extract typename template parameter from type transformation.
 *
 * @author ythrier(at)hsr.ch
 * @author tcorbat(at)hsr.ch
 */

public class ETTPTypeTransform extends Transform {

    private static final String TYPETEMPLATE_EDITGROUP_MSG = "Changing type to template...";
    private static final String TYPEDEF_EDITGROUP_MSG      = "Add typedef...";
    private static final String UNRESOLVABLE_TYPE_MSG      = "Unresolvable type for ";
    private static final String SPACE                      = " ";
    private static final String TYPEDEF_KEYWORD            = "typedef";
    private static final String TEMPLATE_TYPE_NAME_POSTFIX = "T";

    /**
     * {@inheritDoc}
     */
    @Override
    public void postprocess(RefactoringStatus status) {
        if (isTemplate()) {
            checkMissingTypes(status);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preprocess(RefactoringStatus status) {
        super.preprocess(status);
        for (TypeInformation type : getConfig().getAllTypes()) {
            type.setDefaulting(true);
        }

        ICPPASTCompositeTypeSpecifier type = getCompositeTypeSpecifier(originalNode);
        IASTDeclaration[] declarations = type.getDeclarations(false);
        for (IASTDeclaration declaration : declarations) {
            if (declaration instanceof IASTSimpleDeclaration) {
                for (IASTDeclarator declarator : ((IASTSimpleDeclaration) declaration).getDeclarators()) {
                    if (declarator instanceof IASTFunctionDeclarator) {
                        status.addFatalError("Member function '" + declarator.getName().toString() +
                                             "' is not defined inline, it cannot be adapted correctly.");
                    }
                }
            }
        }
    }

    /**
     * Check if there are missing types necessary for defaulting.
     *
     * @param status
     * Refactoring status.
     */
    private void checkMissingTypes(RefactoringStatus status) {
        for (TypeInformation type : getConfig().getAllTypesOrdered()) {
            if (type.shouldDefault() && !type.hasDefaultType()) status.addError(UNRESOLVABLE_TYPE_MSG + type.getTemplateName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTemplateParameter(List<ICPPASTTemplateParameter> templParams, TypeInformation type) {
        if (!getConfig().hasPerformableAction(type)) return;
        ICPPASTSimpleTypeTemplateParameter templParam = createTemplateParam(type);
        if (type.shouldDefault() && type.getCallSpecificDefaultType() != null) templParam.setDefaultType(createTypeId(type));
        templParams.add(templParam);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addConfigChangeActions(List<IConfigChangeAction> configChanges) {
        addNameChangeAction(configChanges);
        addSingleSelectionChangeAction(configChanges);
        configChanges.add(new TypeOrderingChangeAction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modificationPostprocessing(ModificationCollector collector) {
        if (!isTemplate()) {
            renameTypeForNewTemplate();
            addTypedefForNewTemplate(collector);
        }
    }

    /**
     * Add a typedef for the new template type.
     *
     * @param collector
     * Modification collector.
     */
    private void addTypedefForNewTemplate(ModificationCollector collector) {
        CPPASTTemplateId templId = new CPPASTTemplateId(createTypedefName());
        for (TypeInformation type : getConfig().getAllTypesOrdered()) {
            if (type.shouldDefault()) break;
            templId.addTemplateArgument(createTypeId(type));
        }
        CPPASTSimpleDeclaration typedef = createTypedefDeclaration(templId);
        insertAfterType(createRewriter(collector, getUnit()), typedef);
    }

    /**
     * Create the typedef declaration.
     *
     * @param templId
     * Template id.
     * @return Typedef declaration.
     */
    private CPPASTSimpleDeclaration createTypedefDeclaration(CPPASTTemplateId templId) {
        CPPASTSimpleDeclaration typedef = new CPPASTSimpleDeclaration();
        typedef.addDeclarator(new CPPASTDeclarator(getOriginalTypeName()));
        typedef.setDeclSpecifier(new CPPASTNamedTypeSpecifier(templId));
        return typedef;
    }

    /**
     * Create the typedef name.
     *
     * @return Name.
     */
    private IASTName createTypedefName() {
        String typedefName = TYPEDEF_KEYWORD + SPACE + createNewTemplateTypeName();
        return new CPPASTName(typedefName.toCharArray());
    }

    /**
     * Get a copy of the old name of the type before the rename operation.
     *
     * @return Old type name.
     */
    private IASTName getOriginalTypeName() {
        return getCompositeTypeSpecifier(getOriginalNode()).getName().copy();
    }

    /**
     * Insert the typedef after the type.
     *
     * @param rewriter
     * AST rewriter.
     * @param typedef
     * Typedef node.
     */
    private void insertAfterType(ASTRewrite rewriter, IASTNode typedef) {
        final IASTNode templateSource = getOriginalNode();
        IASTNode nextNode = getNextSibling(templateSource);
        rewriter.insertBefore(templateSource.getParent(), nextNode, typedef, createTypedefEditText());
    }

    /**
     * Determines the node, which is sibling to node in its parent's children.
     *
     * @param node
     * {@link IASTNode} to find the next sibling for.
     * @return The next sibling of node. May be null if node is null, the parent
     * of node is null or node is the last child of its parent.
     */
    private IASTNode getNextSibling(final IASTNode node) {
        if (node == null || node.getParent() == null) {
            return null;
        }

        final IASTNode parent = node.getParent();
        final IASTNode[] siblings = parent.getChildren();
        for (int siblingIndex = 1; siblingIndex < siblings.length; siblingIndex++) {
            if (siblings[siblingIndex - 1] == node) {
                return siblings[siblingIndex];
            }
        }

        return null;
    }

    /**
     * Text edit group for adding a typedef.
     *
     * @return Text edit group.
     */
    private TextEditGroup createTypedefEditText() {
        return new TextEditGroup(TYPEDEF_EDITGROUP_MSG);
    }

    /**
     * Rename the type, including its constructors, using the original and
     * append {@link #TEMPLATE_TYPE_NAME_POSTFIX}.
     */
    private void renameTypeForNewTemplate() {
        ICPPASTCompositeTypeSpecifier typeSpecifier = getCompositeTypeSpecifier(getCopyNode());
        final IASTName newTypeName = createNewTypeName();

        renameSpecialMemberFuntions(typeSpecifier, newTypeName);

        typeSpecifier.setName(newTypeName);
    }

    /**
     * Create the new AST name for the type.
     *
     * @return New AST name.
     */
    private IASTName createNewTypeName() {
        return new CPPASTName(createNewTemplateTypeName().toCharArray());
    }

    /**
     * Get the new template type name.
     *
     * @return New name.
     */
    private String createNewTemplateTypeName() {
        String oldName = new String(getCompositeTypeSpecifier(getOriginalNode()).getName().toCharArray());
        return (oldName + TEMPLATE_TYPE_NAME_POSTFIX);
    }

    /**
     * Get the composite specifier from the given node.
     *
     * @param node
     * Node.
     * @return Composite type.
     */
    private ICPPASTCompositeTypeSpecifier getCompositeTypeSpecifier(IASTNode node) {
        return (ICPPASTCompositeTypeSpecifier) ((CPPASTSimpleDeclaration) node).getDeclSpecifier();
    }

    private void renameSpecialMemberFuntions(ICPPASTCompositeTypeSpecifier typeSpecifier, IASTName newTypeName) {
        if (typeSpecifier == null) {
            return;
        }

        String typeName = typeSpecifier.getName().toString();
        for (IASTDeclaration declaration : typeSpecifier.getDeclarations(true)) {
            replaceDeclaratorNames(newTypeName, typeName, declaration);
            replaceDestructorName(newTypeName, typeName, declaration);
        }

    }

    private void replaceDestructorName(IASTName newTypeName, String typeName, IASTDeclaration declaration) {
        final ICPPNodeFactory nodeFactory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        final IASTName newDestructorName = nodeFactory.newName(("~" + newTypeName.toString()).toCharArray());
        replaceDeclaratorNames(newDestructorName, "~" + typeName, declaration);
    }

    private void replaceDeclaratorNames(IASTName newTypeName, String typeName, IASTDeclaration declaration) {
        if (declaration instanceof IASTSimpleDeclaration) {
            IASTSimpleDeclaration simpleDeclaration = (IASTSimpleDeclaration) declaration;
            for (IASTDeclarator declarator : simpleDeclaration.getDeclarators()) {
                renameMatchingName(newTypeName, typeName, declarator);
            }
        } else if (declaration instanceof IASTFunctionDefinition) {
            IASTFunctionDefinition functionDeclaration = (IASTFunctionDefinition) declaration;
            IASTDeclarator declarator = functionDeclaration.getDeclarator();
            renameMatchingName(newTypeName, typeName, declarator);
        }
    }

    private void renameMatchingName(IASTName newName, String name, IASTDeclarator declarator) {
        if (declarator.getName().toString().equals(name)) {
            declarator.setName(newName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextEditGroup createEditText() {
        return new TextEditGroup(TYPETEMPLATE_EDITGROUP_MSG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IASTDeclaration getTemplateBody() {
        return getTemplateBodyDecl(getCopyNode());
    }

    /**
     * Return the template body declaration.
     *
     * @param node
     * Node to get the body declaration from.
     * @return Body declaration.
     */
    private CPPASTSimpleDeclaration getTemplateBodyDecl(IASTNode node) {
        return (CPPASTSimpleDeclaration) node;
    }
}
