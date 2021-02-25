package com.cevelop.includator.helpers;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPTemplateInstance;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.core.runtime.CoreException;


public class BindingHelper {

    public static IASTFileLocation findBindingDefinitionLocation(IBinding binding, IIndex index) {
        try {
            return findName(binding, index, IIndex.FIND_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES).getFileLocation();
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public static IASTFileLocation findBindingDeclarationLocation(IBinding binding, IIndex index) {
        try {
            int flags = IIndex.FIND_DECLARATIONS_DEFINITIONS | IIndex.SEARCH_ACROSS_LANGUAGE_BOUNDARIES;
            return findName(binding, index, flags).getFileLocation();
        } catch (CoreException e) {
            throw new IncludatorException(e);
        }
    }

    public static IIndexName findName(IBinding binding, IIndex index, int flags) throws CoreException {
        IIndexName[] declNames = index.findNames(binding, flags);
        if (declNames.length != 1) {
            throw new IncludatorException("There should be exactly one declaration for '" + binding.getName() + "' but there had been " +
                                          declNames.length);
        }
        return declNames[0];
    }

    public static boolean hasDefinition(IBinding binding, IIndex index) {
        try {
            findBindingDeclarationLocation(binding, index);
            return true;
        } catch (IncludatorException e) {
            return false;
        }
    }

    public static boolean isConstructor(IBinding binding) {
        return binding instanceof ICPPConstructor;
    }

    public static boolean isDestructor(IBinding binding) {
        return binding instanceof ICPPMethod && ((ICPPMethod) binding).isDestructor();
    }

    public static boolean isClassType(IBinding binding) {
        return binding instanceof ICPPClassType;
    }

    public static ICPPClassType getMostSpecificDefiningClassBinding(ICPPClassType classType, IIndex index) {
        while (isTemplateDeclarationWithDefinition(classType, index)) {
            ICPPTemplateInstance teplInst = (ICPPTemplateInstance) classType;
            IBinding specializedBinding = teplInst.getSpecializedBinding();
            if (specializedBinding instanceof ICPPClassType) {
                classType = (ICPPClassType) specializedBinding;
            } else {
                break;
            }
        }

        return classType;
    }

    private static boolean isTemplateDeclarationWithDefinition(ICPPClassType classType, IIndex index) {
        try {
            return classType instanceof ICPPTemplateInstance && index.findNames(classType, IIndex.FIND_DEFINITIONS).length == 0;
        } catch (CoreException e) {
            return false;
        }
    }

    public static ICPPFunction getMostSpecificDefiningFunctionBinding(ICPPFunction method) {
        if (method instanceof ICPPSpecialization) {
            IBinding specializedBinding = ((ICPPSpecialization) method).getSpecializedBinding();
            if (specializedBinding instanceof ICPPMethod) {
                return getMostSpecificDefiningFunctionBinding((ICPPFunction) specializedBinding);
            }
        }
        return method;
    }

    public static IType unwrapType(IType type) {
        if (type instanceof IPointerType) {
            return unwrapType(((IPointerType) type).getType());
        }
        if (type instanceof ICPPReferenceType) {
            return unwrapType(((ICPPReferenceType) type).getType());
        }
        if (type instanceof IQualifierType) {
            return unwrapType(((IQualifierType) type).getType());
        }
        return type;
    }

    public static IBinding adaptBinding(IBinding binding, IIndex index) {
        IIndexBinding adaptedBinding = index.adaptBinding(binding);
        if (adaptedBinding != null) {
            return adaptedBinding;
        }
        return binding;
    }

    public static IBinding unwrapProblemBinding(IBinding resolvedBinding) {
        if (resolvedBinding instanceof IProblemBinding) {
            return unwrapProblemBinding((IProblemBinding) resolvedBinding);
        }

        return resolvedBinding;
    }

    private static IBinding unwrapProblemBinding(IProblemBinding problemBinding) {
        if (problemBinding.getID() == IProblemBinding.SEMANTIC_AMBIGUOUS_LOOKUP) {
            return problemBinding;
        }
        IBinding[] candidates = problemBinding.getCandidateBindings();
        if (candidates.length == 1) {
            return candidates[0];
        }
        return problemBinding;
    }

    public static IType unwrapProblemTypeBinding(IType resolvedType) {
        if (resolvedType instanceof IProblemBinding) {
            IProblemBinding problemBinding = (IProblemBinding) resolvedType;
            IBinding unwrapedBinding = unwrapProblemBinding(problemBinding);
            if (unwrapedBinding instanceof IType) {
                return (IType) unwrapedBinding;
            } else if (unwrapedBinding instanceof ICPPFunction) {
                return ((ICPPFunction) unwrapedBinding).getType();
            }
        }
        return resolvedType;
    }
}
