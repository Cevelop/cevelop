package com.cevelop.clonewar.transformation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTTypeMatcher;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIdExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTNamedTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTypeId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.TextEditGroup;

import com.cevelop.clonewar.transformation.action.TransformAction;
import com.cevelop.clonewar.transformation.configuration.action.FunctionOrderingChangeAction;
import com.cevelop.clonewar.transformation.configuration.action.IConfigChangeAction;
import com.cevelop.clonewar.transformation.util.TypeInformation;
import com.cevelop.clonewar.transformation.util.referencelookup.FunctionNormalReferenceLookupStrategy;
import com.cevelop.clonewar.transformation.util.referencelookup.FunctionSpecializedReferenceLookupStrategy;
import com.cevelop.clonewar.transformation.util.referencelookup.ReferenceLookupStrategy;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


/**
 * Extract typename template parameter from function transformation.
 *
 * @author ythrier(at)hsr.ch
 */

public class ETTPFunctionTransform extends Transform {

    private static final String                                    FUNCTION_EDITGROUP_MSG   = "Changing function to template...";
    private static final String                                    CALLADJUST_EDITGROUP_MSG = "Adjusting call...";
    private ReferenceLookupStrategy<ICPPASTFunctionCallExpression> callReferenceLookup;
    private List<ICPPASTFunctionCallExpression>                    callExpressions;
    private final CRefactoringContext                              context;

    public ETTPFunctionTransform(CRefactoringContext context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postprocess(RefactoringStatus status) {
        if (isTemplate()) {
            callReferenceLookup = new FunctionSpecializedReferenceLookupStrategy(context);
        } else {
            callReferenceLookup = new FunctionNormalReferenceLookupStrategy(context);
        }
        try {
            callExpressions = callReferenceLookup.findAllReferences(getFunctionName());
        } catch (Exception e) {
            status.addError(e.toString());
        }
    }

    /**
     * Return the function name.
     *
     * @return Function name.
     */
    private IASTName getFunctionName() {
        return ((ICPPASTFunctionDefinition) getOriginalNode()).getDeclarator().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modificationPostprocessing(ModificationCollector collector) {
        if (!shouldAdjustCalls()) {
            return;
        }
        for (ICPPASTFunctionCallExpression call : callExpressions) {
            processCall(call, collector);
        }
    }

    /**
     * Check if the calls have to be adjusted. Adjusting calls is only necessary
     * if the return type of the function is not available in the function
     * parameters. If the return type is not generic, no changes have to be
     * made.
     *
     * @return True if the call expressions have to be adjusted, otherwise
     * false.
     */
    private boolean shouldAdjustCalls() {
        TypeInformation returnType = null;
        for (TypeInformation type : getConfig().getAllTypes()) {
            if (getConfig().hasReturnTypeAction(type)) {
                returnType = type;
                break;
            }
        }
        if (returnType == null) {
            return false;
        }
        if (!getConfig().getReturnTypeAction(returnType).shouldPerform()) {
            return false;
        }
        return !hasParamAndReturnAction(getConfig().getActionsOf(returnType));
    }

    /**
     * Check if the return type is in the parameter list. Therefore no
     * adjustment of the calls have to be made.
     *
     * @param actions
     * List of actions.
     * @return True if the return type is in the parameter list, otherwise
     * false.
     */
    private boolean hasParamAndReturnAction(List<TransformAction> actions) {
        for (TransformAction action : actions) {
            if (action.isParameterAction()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Process the replacement of the function call.
     *
     * @param call
     * Function call.
     * @param collector
     * Modification collector.
     */
    private void processCall(ICPPASTFunctionCallExpression call, ModificationCollector collector) {
        List<TypeInformation> typesToIncludeInCall = getTypesToIncludeInCall();
        if (isTemplate()) {
            resolveMissingTypes(typesToIncludeInCall, call);
        }
        adjustCall(typesToIncludeInCall, call, collector);
    }

    /**
     * Get a list of all types to include in the call.
     *
     * @return List of types to include in the call.
     */
    private List<TypeInformation> getTypesToIncludeInCall() {
        List<TypeInformation> types = getConfig().getAllTypesOrdered();
        List<TypeInformation> typesToIncludeInCall = new ArrayList<>();
        for (TypeInformation type : types) {
            if (getConfig().hasReturnTypeAction(type)) {
                typesToIncludeInCall.add(type);
                break;
            }
            typesToIncludeInCall.add(type);
        }
        return typesToIncludeInCall;
    }

    /**
     * Adjust a function call.
     *
     * @param types
     * Types which are included in the call.
     * @param call
     * Function call.
     * @param collector
     * Modification collector.
     */
    private void adjustCall(List<TypeInformation> types, ICPPASTFunctionCallExpression call, ModificationCollector collector) {
        CPPASTTemplateId newFunctionName = new CPPASTTemplateId(getFunctionName().copy());
        for (TypeInformation type : types) {
            newFunctionName.addTemplateArgument(createTypeId(type));
        }
        IASTIdExpression newCallExpr = (CPPASTIdExpression) call.getFunctionNameExpression().copy();
        newCallExpr.setName(newFunctionName);
        createRewriter(collector, getUnitOf(call)).replace(call.getFunctionNameExpression(), newCallExpr, createCallEditText());
    }

    /**
     * Create a call edit text.
     *
     * @return Edit group.
     */
    private TextEditGroup createCallEditText() {
        return new TextEditGroup(CALLADJUST_EDITGROUP_MSG);
    }

    /**
     * Return the translation unit of the call.
     *
     * @param call
     * Function call.
     * @return Translation unit.
     */
    private IASTTranslationUnit getUnitOf(ICPPASTFunctionCallExpression call) {
        return callReferenceLookup.getTranslationUnitOf(call);
    }

    /**
     * Resolve missing types.
     *
     * @param types
     * Type list.
     * @param call
     * Function call.
     */
    private void resolveMissingTypes(List<TypeInformation> types, ICPPASTFunctionCallExpression call) {
        for (TypeInformation type : types) {
            if (!type.hasDefaultType()) {
                resolveMissingType(type, call);
            }
        }
    }

    /**
     * Resolve a missing call using either the parameters or the call.
     *
     * @param type
     * Type.
     * @param call
     * Function call.
     */
    private void resolveMissingType(TypeInformation type, ICPPASTFunctionCallExpression call) {
        if (!isTemplateCall(call)) {
            resolveByParameters(type, call);
            return;
        }
        ICPPASTTemplateParameter[] templParams = getTemplateDeclaration().getTemplateParameters();
        int templateArgumentPos = 0;
        for (ICPPASTTemplateParameter param : templParams) {
            if (isSameType(type.getType(), getBinding(param))) {
                break;
            }
            templateArgumentPos++;
        }
        CPPASTTemplateId funcCallExpr = getTemplateId(call);
        if (templateArgumentPos > (funcCallExpr.getTemplateArguments().length - 1)) {
            resolveByParameters(type, call);
        } else {
            CPPASTTypeId templateArg = (CPPASTTypeId) funcCallExpr.getTemplateArguments()[templateArgumentPos];
            type.setCallSpecificDefaultType(templateArg.getDeclSpecifier());
        }
    }

    /**
     * Return the template id of the call.
     *
     * @param call
     * Function call.
     * @return Template id.
     */
    private CPPASTTemplateId getTemplateId(ICPPASTFunctionCallExpression call) {
        return (CPPASTTemplateId) ((CPPASTIdExpression) call.getFunctionNameExpression()).getName();
    }

    /**
     * Resolve the default type using the parameters.
     *
     * @param type
     * Type.
     * @param call
     * Function call.
     */
    private void resolveByParameters(TypeInformation type, ICPPASTFunctionCallExpression call) {
        int paramPos = 0;
        for (ICPPASTParameterDeclaration param : getFunctionDeclarator().getParameters()) {
            if (isSameType(type.getType(), getType(param))) {
                break;
            }
            paramPos++;
        }
        IASTInitializerClause[] arguments = call.getArguments();
        type.setCallSpecificDefaultType(createDeclSpecifier(getIType(arguments[paramPos])));
    }

    private IType getIType(IASTInitializerClause clause) {
        if (clause == null) {
            return null;
        }
        if (clause instanceof IASTExpression) {
            return ((IASTExpression) clause).getExpressionType();
        }
        if (clause instanceof IASTTypeId) {
            return CPPVisitor.createType(((IASTTypeId) clause).getAbstractDeclarator());
        }
        if (clause instanceof IASTParameterDeclaration) {
            return CPPVisitor.createType(((IASTParameterDeclaration) clause).getDeclarator());
        }
        return null;

    }

    /**
     * Create a declaration specifier for the type.
     *
     * @param paramType
     * Parameter type.
     * @return Declaration specifier.
     */
    private IASTDeclSpecifier createDeclSpecifier(IType paramType) {
        if (paramType instanceof ICPPBasicType) {
            CPPASTSimpleDeclSpecifier declSpec = new CPPASTSimpleDeclSpecifier();
            declSpec.setType(((ICPPBasicType) paramType).getKind());
            return declSpec;
        } else if (paramType instanceof IBinding) {
            CPPASTNamedTypeSpecifier declSpec = new CPPASTNamedTypeSpecifier();
            final IBinding typeBinding = (IBinding) paramType;
            declSpec.setName(new CPPASTName(typeBinding.getNameCharArray()));
            return declSpec;
        } else {
            CPPASTNamedTypeSpecifier declSpec = new CPPASTNamedTypeSpecifier();
            declSpec.setName(new CPPASTName(paramType.toString().toCharArray()));
            return declSpec;
        }
    }

    /**
     * Get a parameter type.
     *
     * @param param
     * Parameter.
     * @return Type.
     */
    private IType getType(ICPPASTParameterDeclaration param) {
        return CPPVisitor.createType(param.getDeclSpecifier());
    }

    /**
     * Return the function declarator.
     *
     * @return Function declarator.
     */
    private CPPASTFunctionDeclarator getFunctionDeclarator() {
        return (CPPASTFunctionDeclarator) ((ICPPASTFunctionDefinition) getOriginalNode()).getDeclarator();
    }

    /**
     * Check if a type and a binding correspond to the same type.
     *
     * @param type
     * Type.
     * @param binding
     * Binding.
     * @return True if the types are equivalent, otherwise false.
     */
    private boolean isSameType(IType type, IBinding binding) {
        return new ASTTypeMatcher().isEquivalent(type, binding);
    }

    /**
     * Check if a two types are the same type.
     *
     * @param type1
     * First type.
     * @param type2
     * Second type.
     * @return True if the types are equivalent, otherwise false.
     */
    private boolean isSameType(IType type1, IType type2) {
        return new ASTTypeMatcher().isEquivalent(type1, type2);
    }

    /**
     * Return the binding for the template parameter.
     *
     * @param param
     * Template parameter.
     * @return Binding.
     */
    private IBinding getBinding(ICPPASTTemplateParameter param) {
        return ((CPPASTSimpleTypeTemplateParameter) param).getName().getBinding();
    }

    /**
     * Check if a function call is a template call. It is possible that a
     * function call is not a template call but the called function is a
     * template. This is the case if all generic template types can be resolved
     * by the function parameters. Therfore the parser will not create a
     * template function call node.
     *
     * @param call
     * Function call.
     * @return True if the node is a template function call, otherwise false.
     */
    private boolean isTemplateCall(ICPPASTFunctionCallExpression call) {
        CPPASTIdExpression functionName = (CPPASTIdExpression) call.getFunctionNameExpression();
        return (functionName.getName() instanceof CPPASTTemplateId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextEditGroup createEditText() {
        return new TextEditGroup(FUNCTION_EDITGROUP_MSG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addConfigChangeActions(List<IConfigChangeAction> configChanges) {
        addNameChangeAction(configChanges);
        addSingleSelectionChangeAction(configChanges);
        configChanges.add(new FunctionOrderingChangeAction());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createTemplateParameter(List<ICPPASTTemplateParameter> templParams, TypeInformation type) {
        if (!getConfig().hasPerformableAction(type)) {
            return;
        }
        ICPPASTSimpleTypeTemplateParameter param = createTemplateParam(type);
        if (type.shouldDefault()) {
            param.setDefaultType(createTypeId(type));
        }
        templParams.add(param);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IASTDeclaration getTemplateBody() {
        return (IASTDeclaration) getCopyNode();
    }
}
