package com.cevelop.charwars.asttools;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;

import com.cevelop.charwars.utils.analyzers.TypeAnalyzer;


public class FunctionBindingAnalyzer {

    public static boolean isValidOverload(ICPPFunction originalOverload, ICPPFunction possibleOverload, int strArgIndex) {
        if (!originalOverload.getName().equals(possibleOverload.getName())) {
            return false;
        }

        boolean matchingVisibility = matchingVisibility(originalOverload, possibleOverload);
        boolean matchingParameters = matchingParameters(originalOverload, possibleOverload, strArgIndex);
        boolean matchingReturnTypes = matchingReturnTypes(originalOverload, possibleOverload);
        return matchingVisibility && matchingParameters && matchingReturnTypes;
    }

    private static boolean matchingVisibility(ICPPFunction originalOverload, ICPPFunction possibleOverload) {
        if (!(originalOverload instanceof ICPPMember) && !(possibleOverload instanceof ICPPMember)) {
            return true;
        }

        if (originalOverload instanceof ICPPMember) {
            int originalOverloadVisibility = ((ICPPMember) originalOverload).getVisibility();
            if (possibleOverload instanceof ICPPMember) {
                int possibleOverloadVisibility = ((ICPPMember) possibleOverload).getVisibility();
                return originalOverloadVisibility == possibleOverloadVisibility;
            }
        }
        return false;
    }

    private static boolean matchingParameters(ICPPFunction originalOverload, ICPPFunction possibleOverload, int strArgIndex) {
        ICPPParameter[] parameters1 = originalOverload.getParameters();
        ICPPParameter[] parameters2 = possibleOverload.getParameters();
        if (parameters1.length != parameters2.length || strArgIndex >= parameters1.length) {
            return false;
        }

        for (int i = 0; i < parameters1.length; ++i) {
            ICPPParameter f1Parameter = parameters1[i];
            ICPPParameter f2Parameter = parameters2[i];

            if (i == strArgIndex) {
                if (!TypeAnalyzer.isStdStringType(f2Parameter.getType())) {
                    return false;
                }
            } else if (!parameterTypesMatch(f1Parameter, f2Parameter)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchingReturnTypes(ICPPFunction originalOverload, ICPPFunction possibleOverload) {
        IType originalOverloadReturnType = originalOverload.getType().getReturnType();
        IType possibleOverloadReturnType = possibleOverload.getType().getReturnType();
        return TypeAnalyzer.matchingTypes(originalOverloadReturnType, possibleOverloadReturnType);
    }

    private static boolean parameterTypesMatch(ICPPParameter originalOverloadParameter, ICPPParameter possibleOverloadParameter) {
        return TypeAnalyzer.matchingTypes(originalOverloadParameter.getType(), possibleOverloadParameter.getType());
    }

    public static IASTName getFunctionName(IASTNode node) {
        IASTName functionName = null;
        if (node instanceof ICPPASTFunctionCallExpression) {
            ICPPASTFunctionCallExpression functionCall = (ICPPASTFunctionCallExpression) node;
            IASTExpression functionNameExpression = functionCall.getFunctionNameExpression();
            if (functionNameExpression instanceof IASTIdExpression) {
                functionName = ((IASTIdExpression) functionNameExpression).getName();
            } else if (functionNameExpression instanceof IASTFieldReference) {
                functionName = ((IASTFieldReference) functionNameExpression).getFieldName();
            }

            if (functionName != null) {
                IBinding functionNameBinding = functionName.resolveBinding();
                if (functionNameBinding instanceof ICPPClassType) {
                    functionName = getConstructorName(functionCall);
                }
            }
        } else if (node instanceof ICPPASTConstructorInitializer) {
            IASTImplicitNameOwner declarator = (IASTImplicitNameOwner) node.getParent();
            functionName = getConstructorName(declarator);
        } else if (node instanceof ICPPASTInitializerList || node instanceof IASTEqualsInitializer) {
            IASTNode parent = node.getParent();

            if (parent instanceof IASTEqualsInitializer) {
                parent = parent.getParent();
            }

            if ((parent instanceof ICPPASTDeclarator || parent instanceof ICPPASTNewExpression) && parent instanceof IASTImplicitNameOwner) {
                IASTImplicitNameOwner declarator = (IASTImplicitNameOwner) parent;
                functionName = getConstructorName(declarator);
            }
        }
        return functionName;
    }

    private static IASTImplicitName getConstructorName(IASTImplicitNameOwner owner) {
        for (IASTImplicitName implicitName : owner.getImplicitNames()) {
            if (implicitName.resolveBinding() instanceof ICPPConstructor) {
                return implicitName;
            }
        }
        return null;
    }

    public static ICPPFunction getFunctionBinding(IASTNode node) {
        IASTName functionName = getFunctionName(node);
        if (functionName != null) {
            IBinding binding = functionName.resolveBinding();
            if (binding instanceof ICPPFunction) {
                return (ICPPFunction) binding;
            }
        }
        return null;
    }

    public static int getArgIndex(IASTNode node, IASTNode idExpression) {
        IASTInitializerClause[] arguments = null;

        if (node instanceof ICPPASTFunctionCallExpression) {
            ICPPASTFunctionCallExpression functionCall = (ICPPASTFunctionCallExpression) node;
            arguments = functionCall.getArguments();
        } else if (node instanceof ICPPASTConstructorInitializer) {
            ICPPASTConstructorInitializer constructorInitializer = (ICPPASTConstructorInitializer) node;
            arguments = constructorInitializer.getArguments();
        } else if (node instanceof ICPPASTInitializerList) {
            ICPPASTInitializerList initializerList = (ICPPASTInitializerList) node;
            arguments = initializerList.getClauses();
        }

        if (arguments != null) {
            return Arrays.asList(arguments).indexOf(idExpression);
        } else if (node instanceof IASTEqualsInitializer) {
            return 0;
        }

        return -1;
    }

    public static IType getParameterType(IASTNode idExpression) {
        idExpression = idExpression.getOriginalNode();
        IASTNode parent = idExpression.getParent();
        int argIndex = getArgIndex(parent, idExpression);
        ICPPFunction functionBinding = getFunctionBinding(parent);

        if (functionBinding != null && argIndex != -1) {
            ICPPParameter[] parameters = functionBinding.getParameters();
            if (argIndex < parameters.length) {
                return parameters[argIndex].getType();
            }
        }
        return null;
    }
}
