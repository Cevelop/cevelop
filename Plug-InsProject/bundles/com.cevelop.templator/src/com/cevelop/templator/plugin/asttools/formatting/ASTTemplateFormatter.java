package com.cevelop.templator.plugin.asttools.formatting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTNodeFactoryFactory;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplatedTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateArgument;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.internal.ui.refactoring.togglefunction.ToggleNodeHelper;

import com.cevelop.templator.plugin.asttools.ASTTools;
import com.cevelop.templator.plugin.asttools.FindFunctionCallExpressionsVisitor;
import com.cevelop.templator.plugin.asttools.data.AbstractResolvedNameInfo;
import com.cevelop.templator.plugin.asttools.data.CPPASTTemplateIdPreservingOriginalNode;
import com.cevelop.templator.plugin.asttools.data.ClassType;
import com.cevelop.templator.plugin.asttools.data.FunctionCall;
import com.cevelop.templator.plugin.asttools.data.LambdaExpression;
import com.cevelop.templator.plugin.asttools.data.MemberFunctionInstance;
import com.cevelop.templator.plugin.asttools.data.ResolvedName;
import com.cevelop.templator.plugin.asttools.data.TemplateInstance;
import com.cevelop.templator.plugin.asttools.templatearguments.TemplateArgumentMap;
import com.cevelop.templator.plugin.logger.TemplatorException;
import com.cevelop.templator.plugin.logger.TemplatorLogger;
import com.cevelop.templator.plugin.util.ReflectionMethodHelper;


public final class ASTTemplateFormatter {

    private static Method getQualifiedNameMethod;

    static {
        try {
            getQualifiedNameMethod = ReflectionMethodHelper.getNonAccessibleMethod(ToggleNodeHelper.class, "getQualifiedName",
                    IASTFunctionDeclarator.class, IASTNode.class);
        } catch (Exception e) {
            TemplatorLogger.errorDialogWithStackTrace("Member function formatting will not work completely.",
                    "Member functions will not have their belonging class name in the declarator.", e);
        }
    }

    private ASTTemplateFormatter() {}

    public static void format(AbstractResolvedNameInfo resolvedName) throws TemplatorException {

        IASTDeclaration definitionCopy = null;
        if (resolvedName instanceof TemplateInstance) {
            definitionCopy = ((TemplateInstance) resolvedName).getDefinition().copy(CopyStyle.withLocations);
            formatTemplateDeclarator((ICPPASTTemplateDeclaration) definitionCopy, ((TemplateInstance) resolvedName).getTemplateArgumentMap());
        } else if (resolvedName instanceof FunctionCall || resolvedName instanceof MemberFunctionInstance || resolvedName instanceof ClassType ||
                   resolvedName instanceof LambdaExpression) {
                       definitionCopy = resolvedName.getDefinition().copy(CopyStyle.withLocations);
                   }
        if (resolvedName.getBinding() instanceof ICPPMethod) {
            formatMethodName(definitionCopy, resolvedName);
        }
        //formatBody(definitionCopy, resolvedName);
        resolvedName.setFormattedDefinition(definitionCopy);
    }

    private static void formatMethodName(IASTDeclaration definitionCopy, AbstractResolvedNameInfo member) throws TemplatorException {
        if (!(member.getParent() instanceof TemplateInstance)) {
            return;
        }

        IASTDeclaration oldDeclaration = definitionCopy;
        boolean isTemplateDeclaration = definitionCopy instanceof ICPPASTTemplateDeclaration;
        if (isTemplateDeclaration) {
            oldDeclaration = ((ICPPASTTemplateDeclaration) definitionCopy).getDeclaration();
        }

        if (oldDeclaration instanceof ICPPASTFunctionDefinition) {
            IASTFunctionDeclarator declarator = ((ICPPASTFunctionDefinition) definitionCopy).getDeclarator();
            IASTName oldDeclaratorName = declarator.getName();
            // not enough time to implement it myself, sorry
            if (!(oldDeclaratorName instanceof ICPPASTQualifiedName)) {
                ICPPASTTemplateDeclaration classTemplateDeclaration = ((TemplateInstance) member.getParent()).getDefinition();
                try {
                    ICPPASTQualifiedName newDeclaratorName = ReflectionMethodHelper.<ICPPASTQualifiedName>invokeStaticMethod(getQualifiedNameMethod,
                            declarator.getOriginalNode(), classTemplateDeclaration.getParent());
                    if (newDeclaratorName != null) {
                        declarator.setName(newDeclaratorName);
                        ((ICPPASTFunctionDefinition) oldDeclaration).setDeclarator(declarator);
                        if (isTemplateDeclaration) {
                            ((ICPPASTTemplateDeclaration) definitionCopy).setDeclaration(oldDeclaration);
                        } else {
                            definitionCopy = oldDeclaration;
                        }
                    }
                } catch (Exception e) {
                    throw new TemplatorException(
                            "Failed to get qualified name for a member function. The class name is thus not shown formatted for " + declarator
                                    .getName() + ".", e);
                }
            }
        }

    }

    private static void formatTemplateDeclarator(ICPPASTTemplateDeclaration templateCopy, TemplateArgumentMap templateArgumentMap)
            throws TemplatorException {
        // normal, non-function template call
        if (templateArgumentMap == null || templateArgumentMap.isEmpty()) {
            return;
        }

        ICPPNodeFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();
        formatTemplateParameters(templateCopy.getTemplateParameters(), templateArgumentMap, factory);
    }

    private static void formatBody(IASTDeclaration definitionCopy, AbstractResolvedNameInfo resolvedName) throws TemplatorException {
        Map<ICPPASTFunctionCallExpression, AbstractResolvedNameInfo> subCallMap = new HashMap<>();

        for (ResolvedName subResolvedNameInfo : resolvedName.getSubNames()) {
            IASTNode subResolvedName = subResolvedNameInfo.getOriginalNode();
            ICPPASTFunctionCallExpression subCall = ASTTools.findFirstAncestorByType(subResolvedName, ICPPASTFunctionCallExpression.class, 5);
            if (subCall != null) {
                subCallMap.put(subCall, subResolvedNameInfo.getInfo());
            }
        }

        FindFunctionCallExpressionsVisitor functionCallFinder = new FindFunctionCallExpressionsVisitor();
        definitionCopy.accept(functionCallFinder);

        for (ICPPASTFunctionCallExpression functionCallToFormat : functionCallFinder.getFunctionCalls()) {
            AbstractResolvedNameInfo templateInfoToFormat = subCallMap.get(functionCallToFormat.getOriginalNode());
            // Some function call expressions do not have a corresponding node found in this plug-in.
            // For example if there is a typename _Allow and then somewhere _Alloc(). If _Alloc is a primitive type
            // it has no definition that can be shown. subCallMap.get is then null hence the first check.
            if (templateInfoToFormat != null && templateInfoToFormat.getBinding() instanceof ICPPTemplateInstance) {
                ICPPTemplateInstance templateInstance = (ICPPTemplateInstance) templateInfoToFormat.getBinding();
                formatCall(functionCallToFormat, templateInstance.getTemplateArguments(), false);
            }
        }
    }

    //adds default template argument for example from typename T  to typename T = Pair<int,int>
    private static void formatTemplateParameters(ICPPASTTemplateParameter[] parameters, TemplateArgumentMap templateArgumentMap,
            ICPPNodeFactory factory) throws TemplatorException {
        for (ICPPASTTemplateParameter parameter : parameters) {
            if (parameter.isParameterPack()) {
                //				ICPPTemplateArgument[] packExpansionArguments = templateArgumentMap.getPackExpansion(parameter);
                //				for (ICPPTemplateArgument argument : packExpansionArguments) {
                //
                //				}
            } else {
                ICPPTemplateArgument argument = templateArgumentMap.getArgument(parameter);
                // could be null for nested typenames that do not have a name, so do not call
                // templateArgumentMap.getArgumentString(parameter) directly since it returns an empty string
                // and we do not know if it was null or not
                if (argument != null) {
                    String argumentString = TemplateArgumentMap.getArgumentString(argument);

                    if (parameter instanceof ICPPASTSimpleTypeTemplateParameter) {
                        addArgument((ICPPASTSimpleTypeTemplateParameter) parameter, argumentString, factory);
                    } else if (parameter instanceof ICPPASTParameterDeclaration) {
                        addArgument((ICPPASTParameterDeclaration) parameter, argumentString, factory);
                    }
                    // nested template parameters as V in template<template<typename T> typename V> void foo(...)
                    else if (parameter instanceof ICPPASTTemplatedTypeTemplateParameter) {
                        addArgument((ICPPASTTemplatedTypeTemplateParameter) parameter, argumentString, templateArgumentMap, factory);
                    }
                }
            }
        }
    }

    public static ICPPASTFunctionCallExpression formatCall(ICPPASTFunctionCallExpression call, ICPPTemplateArgument[] templateArgs, boolean copy) {
        boolean notATemplateFunction = templateArgs == null || templateArgs.length == 0;
        if (notATemplateFunction) {
            return call;
        }

        ICPPASTFunctionCallExpression functionCallCopy = call;
        if (copy) {
            functionCallCopy = call.copy();
        }

        IASTExpression functionNameExpression = functionCallCopy.getFunctionNameExpression();
        if (functionNameExpression instanceof ICPPASTFieldReference) {
            ICPPASTFieldReference fieldReference = (ICPPASTFieldReference) functionNameExpression;
            IASTName methodName = fieldReference.getFieldName();
            ICPPASTName formattedMethodName = addTemplateArgsToName(methodName.getLastName(), templateArgs);
            fieldReference.setFieldName(formattedMethodName);
            functionCallCopy.setFunctionNameExpression(fieldReference);
        }

        else if (functionNameExpression instanceof IASTIdExpression) {
            IASTIdExpression idExpr = (IASTIdExpression) functionNameExpression;
            IASTName fullyQualifiedFunctionName = idExpr.getName();
            ICPPASTName formattedFunctionLastName = addTemplateArgsToName(fullyQualifiedFunctionName.getLastName(), templateArgs);

            IASTName fullyFormattedName;
            if (fullyQualifiedFunctionName instanceof ICPPASTQualifiedName) {
                ((ICPPASTQualifiedName) fullyQualifiedFunctionName).setLastName(formattedFunctionLastName);
                fullyFormattedName = fullyQualifiedFunctionName;
            } else {
                fullyFormattedName = formattedFunctionLastName;
            }

            idExpr.setName(fullyFormattedName);

            functionCallCopy.setFunctionNameExpression(idExpr);
        }

        return functionCallCopy;
    }

    private static void addArgument(ICPPASTTemplatedTypeTemplateParameter parameter, String argumentString, TemplateArgumentMap templateArgumentMap,
            ICPPNodeFactory factory) throws TemplatorException {
        // first add the nested template parameters if any
        formatTemplateParameters(parameter.getTemplateParameters(), templateArgumentMap, factory);
        // now add the default expression for the param itself

        IASTIdExpression newIdExpression = factory.newIdExpression(factory.newName(argumentString.toCharArray()));
        parameter.setDefaultValue(newIdExpression);
    }

    private static void addArgument(ICPPASTSimpleTypeTemplateParameter parameter, String argumentString, ICPPNodeFactory factory) {
        ICPPASTNamedTypeSpecifier nameSpecifier = factory.newTypedefNameSpecifier(factory.newName(argumentString.toCharArray()));
        ICPPASTDeclarator declarator = factory.newDeclarator(factory.newName());
        ICPPASTTypeId defaultType = factory.newTypeId(nameSpecifier, declarator);
        parameter.setDefaultType(defaultType);
    }

    private static void addArgument(ICPPASTParameterDeclaration parameter, String argumentString, ICPPNodeFactory factory) {
        ICPPASTLiteralExpression newLiteralExpression = factory.newLiteralExpression(0, argumentString);
        IASTEqualsInitializer newEqualsInitializer = factory.newEqualsInitializer(newLiteralExpression);
        parameter.getDeclarator().setInitializer(newEqualsInitializer);
    }

    public static ICPPASTTemplateId addTemplateArgsToName(IASTName name, ICPPTemplateArgument[] templateArgs) {
        ICPPNodeFactory factory = ASTNodeFactoryFactory.getDefaultCPPNodeFactory();

        ICPPASTTemplateId templateId = new CPPASTTemplateIdPreservingOriginalNode(name);

        for (ICPPTemplateArgument arg : templateArgs) {
            char[] typeName = "$unknown".toCharArray();
            if (arg != null) {
                typeName = TemplateArgumentMap.getArgumentString(arg).toCharArray();
            }

            ICPPASTNamedTypeSpecifier parameterType = factory.newTypedefNameSpecifier(factory.newName(typeName));

            ICPPASTTypeId argId = factory.newTypeId(parameterType, null);
            templateId.addTemplateArgument(argId);
        }
        return templateId;
    }

}
