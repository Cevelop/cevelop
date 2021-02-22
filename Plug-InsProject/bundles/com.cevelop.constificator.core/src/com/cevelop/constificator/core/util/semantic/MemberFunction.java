package com.cevelop.constificator.core.util.semantic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.util.semantic.Type.InheritanceGraph.ApplicationPolicy;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


@SuppressWarnings("restriction")
public class MemberFunction {

    /**
     * Retrieves the member function of a base class that is overridden if it
     * exists
     *
     * @param function
     * The member function that is suspected to override a member
     * function of a base class
     * @param pointOfInstanciation
     * The point of instantiation required for possible class
     * templates.
     * @return The method that is overridden by the supplied function if it
     * exists, null otherwise
     */
    public static ICPPMethod getOverridden(ICPPMethod function, IASTNode pointOfInstanciation) {
        IType functionType = SemanticUtil.getSimplifiedType(function.getType());
        ReferenceWrapper<ICPPMethod> found = new ReferenceWrapper<>();
        new Type.InheritanceGraph((ICPPClassType) function.getOwner(), pointOfInstanciation).holdsForAny((ICPPClassType type) -> {
            ICPPMethod[] methods = type instanceof ICPPClassType ? type.getDeclaredMethods() : type.getDeclaredMethods();
            for (ICPPMethod memberFunction : methods) {
                if (memberFunction.getName().equals(function.getName()) && memberFunction.isVirtual()) {
                    IType memberType = SemanticUtil.getSimplifiedType(memberFunction.getType());
                    if (memberType.isSameType(functionType)) {
                        found.set(memberFunction);
                        return true;
                    }
                }
            }
            return false;
        });

        return found.get();
    }

    /**
     * Retrieve the base-class member functions that are shadowed by a given
     * function, if any.
     *
     * @param function
     * The member function that is suspected to shadow one or more
     * member functions of its base classes
     * @param pointOfInstanciation
     * The point of instantiation required for possible class
     * templates.
     * @return The set of member functions shadowed by the given function
     */
    public static Set<ICPPMethod> getShadowed(ICPPMethod function, IASTNode pointOfInstanciation) {
        Set<ICPPMethod> shadowed = new HashSet<>();
        ICPPClassType classType = function.getClassOwner();
        ICPPFunctionType functionType = function.getType();

        HashSet<ICPPMethod> ownMethods = new HashSet<>(Arrays.asList(classType.getDeclaredMethods()));

        new Type.InheritanceGraph(function.getClassOwner(), pointOfInstanciation).apply((ICPPClassType type) -> {
            ICPPMethod[] methods = type instanceof ICPPClassType ? type.getDeclaredMethods() : type.getDeclaredMethods();
            for (ICPPMethod memberFunction : methods) {
                if (memberFunction.getName().equals(function.getName())) {
                    IType memberType = SemanticUtil.getSimplifiedType(memberFunction.getType());

                    if ((memberType.isSameType(functionType) && !memberFunction.isVirtual()) || (!memberType.isSameType(functionType) && !ownMethods
                            .contains(memberFunction))) {
                        shadowed.add(memberFunction);
                    }
                }
            }
        }, ApplicationPolicy.SKIP_MOST_DERIVED);

        return shadowed;
    }

    /**
     * Checks if a binding is a <code>const</code> member function.
     *
     * @param suspect
     * The binding to be checked. Must not be <code>null</code>
     * @return <code>true</code> iff. suspect is a <code>const</code> member
     * function.
     */
    public static boolean isConst(IBinding suspect) {
        return suspect instanceof ICPPMethod && ((ICPPMethod) suspect).getType().isConst();
    }

    /**
     * Checks if one member function overrides another member function
     *
     * @param function
     * The member function suspected to override
     * <code>candidate</code>. Must not be <code>null</code>
     * @param pointOfInterest
     * The member function suspected to be overridden by
     * <code>suspect</code>. Must not be <code>null</code>
     * @return <code>true</code> iff. suspect overrides candidate.
     */
    public static boolean overrides(ICPPMethod function, IASTNode pointOfInterest) {
        return MemberFunction.getOverridden(function, pointOfInterest) != null;
    }

    /**
     * Checks if one member function shadows another member function
     *
     * @param function
     * The member function suspected to shadow
     * <code>candidate</code>. Must not be <code>null</code>
     * @param pointOfInstanciation
     * The member function suspected to be shadowed by
     * <code>suspect</code> . Must not be <code>null</code>
     * @return <code>true</code> iff. suspect shadows candidate.
     */
    public static boolean shadows(ICPPMethod function, IASTNode pointOfInstanciation) {
        return !MemberFunction.getShadowed(function, pointOfInstanciation).isEmpty();
    }

    /**
     * Checks if the given member function is a move-ctor
     *
     * @param function
     * The member function to check
     * @return {@code true} iff. the supplied function is a move-ctor, false
     * otherwise
     */
    public static boolean isMoveConstructor(ICPPMethod function) {
        if (!(function instanceof ICPPConstructor)) {
            return false;
        }

        ICPPClassType owner = Cast.as(ICPPClassType.class, function.getOwner());
        ICPPFunctionType ctorType = Cast.as(ICPPFunctionType.class, SemanticUtil.getSimplifiedType(function.getType()));
        if (ctorType == null) {
            return false;
        }

        IType[] parameterTypes = ctorType.getParameterTypes();
        if (parameterTypes.length != 1) {
            return false;
        } else {
            IType simplifiedType = SemanticUtil.getSimplifiedType(parameterTypes[0]);
            if (!(simplifiedType instanceof ICPPReferenceType) || !((ICPPReferenceType) simplifiedType).isRValueReference()) {
                return false;
            } else {
                IType simplifiedReferencedType = SemanticUtil.getSimplifiedType(((ITypeContainer) simplifiedType).getType());
                return simplifiedReferencedType.isSameType(owner);
            }
        }
    }

    /**
     * Checks if the given member function is a move assignment operator
     *
     * @param function
     * The member function to check
     * @return {@code true} iff. the supplied function is a move assignment
     * operator, false otherwise
     */
    public static boolean isMoveAssignmentOperator(ICPPMethod function) {
        ICPPClassType owner = Cast.as(ICPPClassType.class, function.getOwner());
        if (owner == null) {
            return false;
        }

        if (!"operator =".equals(function.getName())) {
            return false;
        }

        ICPPFunctionType operatorType = Cast.as(ICPPFunctionType.class, function.getType());
        IType returnType = SemanticUtil.getSimplifiedType(operatorType.getReturnType());
        if (!(returnType instanceof ICPPReferenceType) || ((ICPPReferenceType) returnType).isRValueReference()) {
            return false;
        } else {
            IType simplifiedReferencedType = SemanticUtil.getSimplifiedType(((ITypeContainer) returnType).getType());
            if (!simplifiedReferencedType.isSameType(owner)) {
                return false;
            }
        }

        IType[] parameterTypes = operatorType.getParameterTypes();
        if (parameterTypes.length != 1) {
            return false;
        } else {
            IType simplifiedType = SemanticUtil.getSimplifiedType(parameterTypes[0]);
            if (!(simplifiedType instanceof ICPPReferenceType) || !((ICPPReferenceType) simplifiedType).isRValueReference()) {
                return false;
            } else {
                IType simplifiedReferencedType = SemanticUtil.getSimplifiedType(((ITypeContainer) simplifiedType).getType());
                return simplifiedReferencedType.isSameType(owner);
            }
        }
    }
}
