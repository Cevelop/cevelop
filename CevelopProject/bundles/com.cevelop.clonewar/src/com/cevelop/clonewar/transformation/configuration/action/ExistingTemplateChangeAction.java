package com.cevelop.clonewar.transformation.configuration.action;

import org.eclipse.cdt.core.dom.ast.ASTTypeMatcher;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPTemplateTypeParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.cdt.internal.core.model.ASTStringUtil;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.cevelop.clonewar.transformation.configuration.TransformConfiguration;
import com.cevelop.clonewar.transformation.util.TypeInformation;


/**
 * Action to propose a template name for the given types. If the type is already
 * a template type, the original name is be proposed.
 *
 * @author ythrier(at)hsr.ch
 */

public class ExistingTemplateChangeAction extends TemplateChangeAction {

    private IASTNode templateDeclaration_;

    /**
     * Create the change action specifing the template declaration.
     *
     * @param templateDeclaration
     * Template declaration node.
     */
    public ExistingTemplateChangeAction(IASTNode templateDeclaration) {
        this.templateDeclaration_ = templateDeclaration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyChange(TransformConfiguration config, RefactoringStatus status) {
        for (ICPPASTTemplateParameter param : getTemplateParameter()) {
            for (TypeInformation type : config.getAllTypes()) {
                if (hasSameType(type, param)) setExistingName(type, param, config);
                tryResolveDefaultType(type, param, config, status);
            }
        }
        proposeUniqueNames(config);
    }

    /**
     * Set an existing template name.
     *
     * @param type
     * Type to set the name.
     * @param param
     * Param with the name.
     * @param config
     * Config to change.
     */
    private void setExistingName(TypeInformation type, ICPPASTTemplateParameter param, TransformConfiguration config) {
        ICPPASTTemplateParameter[] paramArray = new ICPPASTTemplateParameter[] { param };
        updateName(type, config, ASTStringUtil.getTemplateParameterArray(paramArray)[0]);
    }

    /**
     * Try to resolve the default type. The default type may be set to
     * <code>null</code> if the type can not be resolved. In this case, the type
     * is resolved during call adjustment based on the type/function call.
     *
     * @param type
     * Type information.
     * @param param
     * Template parameter.
     * @param status
     * Refactoring status.
     * @param config
     * Transformation config.
     */
    private void tryResolveDefaultType(TypeInformation type, ICPPASTTemplateParameter param, TransformConfiguration config,
            RefactoringStatus status) {
        if (!isTemplateType(type))
            type.setDefaultType(getDefaultTypeOf(type, config, status));
        else type.setDefaultType(getDefaultTypeOf(param));
    }

    /**
     * Check if the type info is a template type (mixed template).
     *
     * @param type
     * Type info.
     * @return True if the type is a template type, otherwise false.
     */
    private boolean isTemplateType(TypeInformation type) {
        return (type.getType() instanceof CPPTemplateTypeParameter);
    }

    /**
     * Return the default type of the template parameter.
     *
     * @param param
     * Template param.
     * @return Default type.
     */
    private IASTDeclSpecifier getDefaultTypeOf(ICPPASTTemplateParameter param) {
        IASTTypeId defaultType = ((ICPPASTSimpleTypeTemplateParameter) param).getDefaultType();
        if (defaultType == null) return null;
        return defaultType.getDeclSpecifier();
    }

    /**
     * Check if a type info and a template parameter correspond to the same
     * type.
     *
     * @param type
     * Type info.
     * @param param
     * Template paramater.
     * @return True if the type info and the template parameter correspond to
     * the same type, otherwise false.
     */
    private boolean hasSameType(TypeInformation type, ICPPASTTemplateParameter param) {
        CPPTemplateDefinition templDefBinding = createTypeBinding(type.getType());
        if (templDefBinding == null) return false;
        IBinding templBinding = createTemplateBinding(param);
        if (new ASTTypeMatcher().isEquivalent(templBinding, type.getType())) return true;
        return false;
    }

    /**
     * Create a template binding for a template parameter.
     *
     * @param param
     * Template parameter.
     * @return Template binding.
     */
    private IBinding createTemplateBinding(ICPPASTTemplateParameter param) {
        return CPPVisitor.createBinding(((CPPASTSimpleTypeTemplateParameter) param).getName());
    }

    /**
     * Resolve the type binding of a type group to an template parameter.
     *
     * @param type
     * Type group.
     * @return Function template or null if there is no binding.
     */
    private CPPTemplateDefinition createTypeBinding(IType type) {
        IBinding binding = null;
        IASTName typeDefName = findTypeDefinition(type);
        if (typeDefName == null) return null;
        binding = typeDefName.getBinding();
        if (binding != null) binding = binding.getOwner();
        return (CPPTemplateDefinition) binding;
    }

    /**
     * Find the type definition of a type info.
     *
     * @param type
     * Type.
     * @return AST name.
     */
    private IASTName findTypeDefinition(IType type) {
        if (type instanceof CPPTemplateTypeParameter) return (IASTName) ((CPPTemplateTypeParameter) type).getDefinition();
        return null;
    }

    /**
     * Return the template parameters of the template declaration.
     *
     * @return Template parameter array.
     */
    private ICPPASTTemplateParameter[] getTemplateParameter() {
        return getTemplateDeclaration().getTemplateParameters();
    }

    /**
     * Return the template declaration node.
     *
     * @return Template declaration.
     */
    private ICPPASTTemplateDeclaration getTemplateDeclaration() {
        return (ICPPASTTemplateDeclaration) templateDeclaration_;
    }
}
