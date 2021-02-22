package com.cevelop.templator.plugin.asttools.resolving;

import static org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil.getSimplifiedType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression.ValueCategory;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameterPackType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateParameterMap;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateTypeParameter;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPParameterPackType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPPointerType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPQualifierType;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPEvaluation;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.cdt.internal.core.dom.parser.cpp.InstantiationContext;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPTemplates;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.EvalPackExpansion;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.LookupData;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.TypeOfDependentExpression;

import com.cevelop.templator.plugin.util.ReflectionMethodHelper;


/**
 * LookupData that can be used to resolve calls that are dependent on template arguments.
 */
public class TemplateContextLookupData extends LookupData {

    private final ICPPTemplateParameterMap templateParameterMap;
    private final IType                    nonDeferredImpliedObjectType;
    // because it's private in the parent class and cached there which causes a problem
    private ICPPEvaluation[] functionArgs;
    private IType[]          functionArgTypes;

    public TemplateContextLookupData(IASTName name, ICPPTemplateParameterMap templateParameterMap, IType impliedObjectType) {
        super(name);
        this.templateParameterMap = templateParameterMap;
        nonDeferredImpliedObjectType = impliedObjectType;
        // fTemplateArguments are the ones from the template-id. They also need to be replaced because they are passed
        // to CPPTemplates.instantiateForFunctionCall inside CPPSemantics.resolveFunction
        Field templateArgumentsField;
        ICPPTemplateArgument[] templateArguments = null;
        try {
            templateArgumentsField = ReflectionMethodHelper.getNonAccessibleField(LookupData.class, "fTemplateArguments");
            templateArguments = (ICPPTemplateArgument[]) templateArgumentsField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (templateArguments != null) {
            for (int i = 0; i < templateArguments.length; i++) {
                ICPPTemplateArgument existingTemplateArgument = templateArguments[i];
                IType templateParameter = existingTemplateArgument.getTypeValue();
                if (templateParameter instanceof ICPPTemplateParameter) {
                    ICPPTemplateArgument replacedArgument = templateParameterMap.getArgument((ICPPTemplateParameter) templateParameter);
                    if (replacedArgument != null) {
                        templateArguments[i] = replacedArgument;
                    }
                }
            }
        }
    }

    @Override
    public IType[] getFunctionArgumentTypes() {
        CPPSemantics.pushLookupPoint(getLookupPoint());
        try {
            if (functionArgTypes == null) {
                if (functionArgTypes == null && functionArgs != null) {
                    functionArgTypes = new IType[functionArgs.length];
                    for (int i = 0; i < functionArgs.length; i++) {
                        ICPPEvaluation e = functionArgs[i];
                        functionArgTypes[i] = getSimplifiedType(e.getType());
                    }
                }
                IType[] functionArgumentTypes = super.getFunctionArgumentTypes();
                if (CPPTemplates.containsDependentType(functionArgumentTypes)) {
                    functionArgumentTypes = replaceDependentTypes(functionArgumentTypes);
                }
            }
        } finally {
            CPPSemantics.popLookupPoint();
        }
        return functionArgTypes;
    }

    @Override
    public ValueCategory[] getFunctionArgumentValueCategories() {
        return super.getFunctionArgumentValueCategories();
    }

    private IType[] replaceDependentTypes(IType[] functionArgumentTypes) {
        IType[] types = new IType[functionArgumentTypes.length];
        for (int i = 0; i < functionArgumentTypes.length; i++) {
            IType adaptedType = replaceDependentType(functionArgumentTypes[i]);
            types[i] = adaptedType;
        }
        return types;
    }

    private IType replaceDependentType(IType functionArgumentType) {
        IType adaptedType = functionArgumentType;
        if (adaptedType instanceof CPPQualifierType) {
            CPPQualifierType qualifierType = (CPPQualifierType) adaptedType;
            return new CPPQualifierType(replaceDependentType(qualifierType.getType()), qualifierType.isConst(), qualifierType.isVolatile());
        }
        if (adaptedType instanceof CPPPointerType) {
            CPPPointerType pointerType = (CPPPointerType) adaptedType;
            return new CPPPointerType(replaceDependentType(pointerType.getType()), pointerType.isConst(), pointerType.isVolatile(), pointerType
                    .isRestrict());
        }
        if (adaptedType instanceof ICPPTemplateTypeParameter) {
            ICPPTemplateArgument replacedType = templateParameterMap.getArgument((ICPPTemplateTypeParameter) adaptedType);
            if (replacedType != null) {
                adaptedType = replacedType.getTypeValue();
            }
        }
        if (adaptedType instanceof ICPPParameterPackType) {

        }
        if (functionArgumentType instanceof TypeOfDependentExpression) {
            InstantiationContext context = new InstantiationContext(templateParameterMap, 0, null);
            return ((TypeOfDependentExpression) functionArgumentType).getEvaluation().instantiate(context, 255).getType();
        }
        return adaptedType;
    }

    @Override
    public void setFunctionArguments(boolean containsImpliedObject, ICPPEvaluation... exprs) {
        // this first call is needed so that LookupData.getFunctionArgumentTypes returns the value for the new
        // expressions
        super.setFunctionArguments(containsImpliedObject, exprs);
        ICPPEvaluation[] instantiatedFunctionArguments = getInstantiantiatedDependentFunctionArguments(exprs);
        super.setFunctionArguments(containsImpliedObject, instantiatedFunctionArguments);
        functionArgs = instantiatedFunctionArguments;
    }

    private ICPPEvaluation[] getInstantiantiatedDependentFunctionArguments(ICPPEvaluation... exprs) {
        List<ICPPEvaluation> newFunctionArgs = new ArrayList<>(exprs.length); // is at least as big as exprs.length
        IType[] functionArgumentTypes = super.getFunctionArgumentTypes();

        // Parameter packs can result in more than one function argument. An evaluation does not result always in one
        // function argument.
        for (int expressionIndex = 0; expressionIndex < exprs.length; expressionIndex++) {
            IType argumentType = functionArgumentTypes[expressionIndex];
            if (argumentType instanceof TypeOfDependentExpression || argumentType instanceof ICPPTemplateParameter) {
                InstantiationContext context = new InstantiationContext(templateParameterMap, -1, null);
                newFunctionArgs.add(exprs[expressionIndex].instantiate(context, 255));
            } else if (argumentType instanceof CPPParameterPackType) {
                while (argumentType instanceof ICPPParameterPackType) {
                    argumentType = ((ICPPParameterPackType) argumentType).getType();
                }
                // TODO msyfrig 15.09.2015 - maybe add the qualifiers from the parameter pack to the extracted arguments
                argumentType = SemanticUtil.getUltimateType(argumentType, false);
                if (argumentType instanceof ICPPTemplateParameter) {
                    ICPPTemplateArgument[] packExpansion = templateParameterMap.getPackExpansion((ICPPTemplateParameter) argumentType);
                    for (int i = 0; i < packExpansion.length; i++) {
                        InstantiationContext context = new InstantiationContext(templateParameterMap, i, null);
                        ICPPEvaluation evalParamPack = exprs[expressionIndex].instantiate(context, 255);
                        if (evalParamPack instanceof EvalPackExpansion) {
                            ICPPEvaluation argumentEval = ((EvalPackExpansion) evalParamPack).getExpansionPattern();
                            newFunctionArgs.add(argumentEval);
                        }
                    }
                }
            } else {
                newFunctionArgs.add(exprs[expressionIndex]);
            }

        }
        return newFunctionArgs.toArray(new ICPPEvaluation[newFunctionArgs.size()]);
    }

    @Override
    public IType getImpliedObjectType() {
        IType impliedObjectType = super.getImpliedObjectType();
        IType ultimateType = SemanticUtil.getUltimateType(impliedObjectType, false);
        if (ultimateType instanceof ICPPUnknownBinding) {
            setImpliedObjectType(impliedObjectType);
        }
        return super.getImpliedObjectType();
    }

    @Override
    public void setImpliedObjectType(IType impliedObjectType) {
        IType replacedImpliedObjectType = impliedObjectType;
        if (nonDeferredImpliedObjectType != null) {
            if (impliedObjectType instanceof ICPPUnknownBinding) {
                replacedImpliedObjectType = nonDeferredImpliedObjectType;
            }
            if (impliedObjectType instanceof ITypeContainer) {
                IType ultimateType = SemanticUtil.getUltimateType(impliedObjectType, false);
                if (ultimateType instanceof ICPPUnknownBinding) {
                    replacedImpliedObjectType = SemanticUtil.replaceNestedType((ITypeContainer) impliedObjectType, nonDeferredImpliedObjectType);
                }
            }
        }
        super.setImpliedObjectType(replacedImpliedObjectType);
    }

    // private ICPPEvaluation[] replaceDependentArguments(IASTInitializerClause[] originalArguments) {
    // ICPPEvaluation[] functionArgs = new ICPPEvaluation[originalArguments.length];
    // for (int i = 0; i < functionArgs.length; i++) {
    // functionArgs[i] = ((ICPPASTInitializerClause) originalArguments[i]).getEvaluation();
    // }
    //
    // functionArgs[i]
    // if (functionArgs != null) {
    // functionArgTypes= new IType[functionArgs.length];
    // for (int i = 0; i < functionArgs.length; i++) {
    // ICPPEvaluation e = functionArgs[i];
    // functionArgTypes[i]= getSimplifiedType(e.getTypeOrFunctionSet(getLookupPoint()));
    // }
    // }
    //
    //
    // return null;
    // }

    public static LookupData createLookupData(IASTName name, ICPPTemplateParameterMap templateParameterMap, IType impliedObjectType) {
        LookupData data = new TemplateContextLookupData(name, templateParameterMap, impliedObjectType);
        IASTNode parent = name.getParent();

        if (parent instanceof ICPPASTTemplateId) {
            parent = parent.getParent();
        }
        if (parent instanceof ICPPASTQualifiedName) {
            parent = parent.getParent();
        }

        if (parent instanceof IASTDeclarator && parent.getPropertyInParent() == IASTSimpleDeclaration.DECLARATOR) {
            IASTSimpleDeclaration simple = (IASTSimpleDeclaration) parent.getParent();
            if (simple.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {
                data.qualified = true;
            }
        }

        if (parent instanceof IASTIdExpression) {
            IASTNode grand = parent.getParent();
            while (grand instanceof IASTUnaryExpression && ((IASTUnaryExpression) grand).getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                parent = grand;
                grand = grand.getParent();
            }
            if (parent.getPropertyInParent() == IASTFunctionCallExpression.FUNCTION_NAME) {
                parent = parent.getParent();
                IASTInitializerClause[] args = ((IASTFunctionCallExpression) parent).getArguments();
                // if (CPPTemplates.containsDependentType(functionArgumentTypes)) {
                // return replaceDependentTypes(functionArgumentTypes);
                // }
                data.setFunctionArguments(false, args);
            }
        } else if (parent instanceof ICPPASTFieldReference) {
            IASTNode grand = parent.getParent();
            while (grand instanceof IASTUnaryExpression && ((IASTUnaryExpression) grand).getOperator() == IASTUnaryExpression.op_bracketedPrimary) {
                parent = grand;
                grand = grand.getParent();
            }
            if (parent.getPropertyInParent() == IASTFunctionCallExpression.FUNCTION_NAME) {
                IASTInitializerClause[] exp = ((IASTFunctionCallExpression) parent.getParent()).getArguments();
                data.setFunctionArguments(false, exp);
            }
        } else if (parent instanceof ICPPASTNamedTypeSpecifier && parent.getParent() instanceof IASTTypeId) {
            IASTTypeId typeId = (IASTTypeId) parent.getParent();
            if (typeId.getParent() instanceof ICPPASTNewExpression) {
                ICPPASTNewExpression newExp = (ICPPASTNewExpression) typeId.getParent();
                IASTInitializer init = newExp.getInitializer();
                if (init == null) {
                    data.setFunctionArguments(false, new ICPPEvaluation[] {});
                } else if (init instanceof ICPPASTConstructorInitializer) {
                    data.setFunctionArguments(false, ((ICPPASTConstructorInitializer) init).getArguments());
                } else if (init instanceof ICPPASTInitializerList) {
                    data.setFunctionArguments(false, (ICPPASTInitializerList) init);
                }
            }
        } else if (parent instanceof ICPPASTConstructorChainInitializer) {
            ICPPASTConstructorChainInitializer ctorinit = (ICPPASTConstructorChainInitializer) parent;
            IASTInitializer init = ctorinit.getInitializer();
            if (init instanceof ICPPASTConstructorInitializer) {
                data.setFunctionArguments(false, ((ICPPASTConstructorInitializer) init).getArguments());
            } else if (init instanceof ICPPASTInitializerList) {
                data.setFunctionArguments(false, (ICPPASTInitializerList) init);
            }
        }

        return data;
    }

}
