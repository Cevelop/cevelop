package com.cevelop.elevator.ast.analysis.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBasicType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPClassType;


/**
 * Narrowing type conversion: conversion with precision loss, e.g. int x = 3.2;
 * In contrast to copy initialization, initializer lists do not support
 * narrowing conversions. int x = 3.2; // valid int x { 3.2 }; // invalid
 */
public class HasNarrowingTypeConversion extends Condition {

    @Override
    public boolean satifies(final IASTNode node) {
        if (node instanceof ICPPASTConstructorChainInitializer) {
            return satifies((ICPPASTConstructorChainInitializer) node);
        }
        if (node instanceof IASTDeclarator) {
            return satifies((IASTDeclarator) node);
        }
        return false;
    }

    private boolean satifies(ICPPASTConstructorChainInitializer node) {
        IBinding binding = node.getMemberInitializerId().resolveBinding();
        IType targetType = getType(binding);
        List<IType> sourceTypes = getSourceTypes(node.getInitializer());
        return hasNarrowingTypeConversion(targetType, sourceTypes);
    }

    private boolean satifies(IASTDeclarator node) {
        IASTInitializer initializer = node.getInitializer();
        if (initializer != null && (initializer instanceof IASTEqualsInitializer || initializer instanceof ICPPASTConstructorInitializer)) {
            IType targetType = getType(node.getName());
            List<IType> sourceTypes = getSourceTypes(initializer);
            return hasNarrowingTypeConversion(targetType, sourceTypes);
        }
        return false;
    }

    private boolean hasNarrowingTypeConversion(IType targetType, List<IType> sourceTypes) {
        IType absoluteTargetType = getAbsoluteType(targetType);
        return sourceTypes.isEmpty() || !isTypeCompatible(getConvertibleTargetTypes(absoluteTargetType), sourceTypes);
    }

    private IType getType(IASTName name) {
        IBinding binding = name.resolveBinding();
        return getType(binding);
    }

    private IType getType(IBinding binding) {
        if (binding instanceof IType) {
            return (IType) binding;
        } else if (binding instanceof IVariable) {
            return ((IVariable) binding).getType();
        } else if (binding instanceof ICPPConstructor) {
            return ((ICPPConstructor) binding).getType();
        }
        return null;
    }

    private boolean hasNarrowingTypeConversion(IBasicType target, IBasicType source) {
        return !(isEqualType(target, source) || isFloatToDoubleConversion(target, source));
    }

    private boolean isEqualType(IBasicType target, IBasicType source) {
        return target.getKind() == source.getKind();
    }

    private boolean isFloatToDoubleConversion(IBasicType target, IBasicType source) {
        return target.getKind() == IBasicType.Kind.eDouble && source.getKind() == IBasicType.Kind.eFloat;
    }

    private List<IType> getSourceTypes(IASTInitializer initializer) {
        if (initializer instanceof IASTEqualsInitializer) {
            return getEqualsInitializerSourceTypes((IASTEqualsInitializer) initializer);
        }
        if (initializer instanceof ICPPASTConstructorInitializer) {
            return getConstructorInitializerSourceTypes((ICPPASTConstructorInitializer) initializer);
        }
        return Collections.emptyList();
    }

    private List<IType> getConstructorInitializerSourceTypes(ICPPASTConstructorInitializer initializer) {
        ArrayList<IType> sourceTypes = new ArrayList<>();
        for (IASTInitializerClause clause : initializer.getArguments()) {
            if (clause instanceof IASTExpression) {
                sourceTypes.add(((IASTExpression) clause).getExpressionType());
            }
        }
        return sourceTypes;
    }

    private List<IType> getEqualsInitializerSourceTypes(IASTEqualsInitializer initializer) {
        List<IType> sourceTypes = new ArrayList<>();
        IASTInitializerClause clause = initializer.getInitializerClause();
        if (clause instanceof IASTExpression) {
            sourceTypes.add(((IASTExpression) clause).getExpressionType());
        }
        return sourceTypes;
    }

    private boolean isTypeCompatible(List<List<IType>> convertibleTypes, List<IType> sourceTypes) {
        for (List<IType> constructorTypes : convertibleTypes) {
            if (constructorTypes.size() == sourceTypes.size() && hasEqualType(sourceTypes, constructorTypes)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEqualType(List<IType> sourceTypes, List<IType> constructorTypes) {
        for (int i = 0; i < constructorTypes.size(); i++) {
            IType targetType = constructorTypes.get(i);
            IType sourceType = sourceTypes.get(i);
            if (targetType instanceof IBasicType && sourceType instanceof IBasicType) {
                if (hasNarrowingTypeConversion(((IBasicType) targetType), (IBasicType) sourceType)) {
                    return false;
                }
            } else if (!targetType.equals(sourceType)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Inspect target type constructors taking a single argument, and return
     * list of found types.
     */
    private List<List<IType>> getConvertibleTargetTypes(final IType targetType) {
        List<List<IType>> convertibleTypes = new ArrayList<>();

        if (targetType instanceof ICPPBasicType) {
            ArrayList<IType> typeList = new ArrayList<>();
            typeList.add(targetType);
            convertibleTypes.add(typeList);
        }

        if (targetType instanceof ICPPClassType) {
            ICPPConstructor[] constructors = getTypeConstructors(targetType);
            if (constructors.length == 0) {
                if (targetType instanceof ICPPTemplateInstance) {
                    ArrayList<IType> typeList = new ArrayList<>();
                    for (ICPPTemplateArgument argument : ((ICPPTemplateInstance) targetType).getTemplateArguments()) {
                        if (argument.getTypeValue() != null) {
                            typeList.add(argument.getTypeValue());
                        }
                    }
                    convertibleTypes.add(typeList);
                }
                return convertibleTypes;
            }
            for (ICPPConstructor decl : constructors) {
                ArrayList<IType> typeList = new ArrayList<>();
                for (ICPPParameter param : decl.getParameters()) {
                    IType type = getAbsoluteType(param.getType());
                    typeList.add(type);
                }
                convertibleTypes.add(typeList);
            }
        } else if (targetType instanceof ICPPFunctionType) {
            IType[] parameterTypes = ((ICPPFunctionType) targetType).getParameterTypes();
            convertibleTypes.add(Arrays.asList(parameterTypes));
        }
        return convertibleTypes;
    }

    /**
     * extracts the real type from references and qualifiers.
     */
    private IType getAbsoluteType(IType type) {
        if (type instanceof ICPPReferenceType) {
            type = ((ICPPReferenceType) type).getType();
        }
        if (type instanceof IQualifierType) {
            type = ((IQualifierType) type).getType();
        }
        return type;
    }

    /**
     * Gets the constructors of the target type of a declaration.
     */
    private ICPPConstructor[] getTypeConstructors(IType targetType) {

        /*
         * FIXME
         * While we should be using a test for ICPPClassType and not
         * CPPClassType, this results in an exception on getConstructors().
         */
        return targetType instanceof CPPClassType ? ((ICPPClassType) targetType).getConstructors() : new ICPPConstructor[0];
    }

}
