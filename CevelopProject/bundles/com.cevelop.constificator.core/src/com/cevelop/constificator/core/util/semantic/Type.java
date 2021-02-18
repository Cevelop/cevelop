package com.cevelop.constificator.core.util.semantic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.dom.ast.IPointerType;
import org.eclipse.cdt.core.dom.ast.IQualifierType;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunctionType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPPointerToMemberType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPReferenceType;
import org.eclipse.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.SemanticUtil;

import com.cevelop.constificator.core.util.functional.UnaryFunction;
import com.cevelop.constificator.core.util.functional.UnaryPredicate;
import com.cevelop.constificator.core.util.type.Cast;
import com.cevelop.constificator.core.util.type.ReferenceWrapper;


/**
 * A collection of function to determine semantic properties of types
 */
@SuppressWarnings("restriction")
public class Type {

    /**
     * Objects of this class represent the inheritance graph of a class type
     * <p>
     * The objects allow the user to apply functions to all nodes (types) in the
     * graph and to evaluate whether a given predicate holds for any of the
     * contained types.
     */
    public static class InheritanceGraph {

        public static enum ApplicationPolicy {
            /**
             * Potentially visit virtually inherited types multiple times
             * <p>
             * Per default, virtually inherited types are visited exactly once
             * during graph traversal. This policy element causes virtually
             * inherited types to be visited via each inheriting type.
             * <p>
             * Consider the following inheritance hierarchy, where both B and C
             * inherit virtually from A and D inherits (non-virtually) from B
             * and C:
             *
             * <pre>
             *     A
             *    / \
             *   B   C
             *    \ /
             *     D
             * </pre>
             * <p>
             * Using this policy element would cause A to be visited twice.
             * 
             */
            DONT_SKIP_VIRTUAL,
            /**
             * Skip the most derived type during graph traversal
             * <p>
             * Normally, all nodes in the graph, including the most derived
             * type, are traversed. This might be undesirable since the user
             * might require to not visit the most derived type.
             */
            SKIP_MOST_DERIVED,
        }

        private static class TypeNode {

            private final List<TypeNode> fBaseClasses;
            private final boolean        fIsVirtual;
            private final IType          fType;

            TypeNode(IType type, boolean isVirtual) {
                fType = type;
                fIsVirtual = isVirtual;
                fBaseClasses = new ArrayList<>();
            }

            void addBaseClass(TypeNode node) {
                fBaseClasses.add(node);
            }

            @SafeVarargs
            final void apply(UnaryFunction<ICPPClassType> function, ApplicationPolicy... policy) {
                EnumSet<ApplicationPolicy> safePolicy = policy.length > 0 ? EnumSet.of(policy[0], policy) : EnumSet.noneOf(ApplicationPolicy.class);
                Set<TypeNode> skipSet = new HashSet<>();
                if (safePolicy.contains(ApplicationPolicy.SKIP_MOST_DERIVED)) {
                    skipSet.add(this);
                }
                doApply(function, skipSet, safePolicy);
            }

            void doApply(UnaryFunction<ICPPClassType> function, Set<TypeNode> skipSet, EnumSet<ApplicationPolicy> policy) {
                for (TypeNode baseClass : fBaseClasses) {
                    baseClass.doApply(function, skipSet, policy);
                }

                if (!skipSet.contains(this)) {
                    ICPPClassType classType = Cast.as(ICPPClassType.class, fType);
                    if (classType != null) {
                        function.call(classType);
                    }

                    if (fIsVirtual && !policy.contains(ApplicationPolicy.DONT_SKIP_VIRTUAL)) {
                        skipSet.add(this);
                    }
                }
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof TypeNode)) {
                    return false;
                }

                TypeNode other = (TypeNode) obj;
                return fType.isSameType(other.fType);
            }

            @Override
            public int hashCode() {
                return fType.hashCode() + Boolean.hashCode(fIsVirtual);
            }

            @Override
            public String toString() {
                boolean hasBases = !fBaseClasses.isEmpty();
                StringBuilder descriptionBuilder = new StringBuilder("TypeNode(" + fType.toString() + ")");

                if (hasBases) {
                    descriptionBuilder.append("->[ ");
                    for (int baseIndex = 0; baseIndex < fBaseClasses.size(); ++baseIndex) {
                        TypeNode base = fBaseClasses.get(baseIndex);
                        descriptionBuilder.append(base);
                        descriptionBuilder.append("{" + (base.fIsVirtual ? "virtual" : "non-virtual") + "}");
                        if (baseIndex < fBaseClasses.size() - 1) {
                            descriptionBuilder.append(", ");
                        }
                    }
                    descriptionBuilder.append(" ]");
                }

                return descriptionBuilder.toString();
            }

        }

        private final TypeNode       fEntryPoint;
        private final List<TypeNode> fVirtualBases;

        /**
         * Create the inheritance graph for the given type without resolving any
         * template instantiations.
         * <p>
         * This is equivalent to calling
         * {@link InheritanceGraph#InheritanceGraph(ICPPClassType, IASTNode)} with null as its
         * second argument.
         *
         * @param type
         * The type for which to generate the inheritance graph
         * @see InheritanceGraph#InheritanceGraph(ICPPClassType, IASTNode)
         */
        public InheritanceGraph(ICPPClassType type) {
            this(type, null);
        }

        /**
         * Create the inheritance graph for the given type resolving any
         * template instantiations at the given source location
         *
         * @param type
         * The type for which to generate the inheritance graph
         * @param pointOfInstanciation
         * The source location for which to instantiate possible
         * template classes
         */
        public InheritanceGraph(ICPPClassType type, IASTNode pointOfInstanciation) {
            fEntryPoint = new TypeNode(type, false);
            fVirtualBases = new ArrayList<>();
            buildSubGraph(fEntryPoint);
        }

        /**
         * Apply a unary function to all nodes of the inheritance graph
         * respecting the given application policy
         * <p>
         * Example:
         *
         * <pre>
         * {@code
         * someTypeGraph.apply((ICPPClassType type) -> {
         *   System.out.println(type);
         *   }, ApplicationPolicy.SKIP_MOST_DERIVED);
         * }
         * </pre>
         *
         * @param function
         * The function to apply
         * @param policy
         * The policy describing how to apply the function
         * @see ApplicationPolicy
         */
        @SafeVarargs
        public final void apply(UnaryFunction<ICPPClassType> function, ApplicationPolicy... policy) {
            fEntryPoint.apply(function, policy);
        }

        private void buildSubGraph(TypeNode entryPoint) {
            ICPPClassType type = Cast.as(ICPPClassType.class, entryPoint.fType);
            if (type == null) {
                return;
            }

            List<TypeNode> inserted = new ArrayList<>();
            ICPPBase[] bases = type instanceof ICPPClassType ? type.getBases() : type.getBases();

            for (ICPPBase base : bases) {
                boolean isVirtualBase = base.isVirtual();
                IType baseType = base.getBaseClassType();
                TypeNode baseNode = new TypeNode(baseType, isVirtualBase);

                if (isVirtualBase) {
                    if (!fVirtualBases.contains(baseNode)) {
                        fVirtualBases.add(baseNode);
                        inserted.add(baseNode);
                    }

                    entryPoint.addBaseClass(fVirtualBases.get(fVirtualBases.indexOf(baseNode)));
                } else {
                    entryPoint.addBaseClass(baseNode);
                    inserted.add(baseNode);
                }
            }

            for (TypeNode node : inserted) {
                buildSubGraph(node);
            }
        }

        /**
         * Check whether the given predicate holds for any type in the
         * inheritance graph
         * <p>
         * Example:
         *
         * <pre>
         * {@code
         * someTypeGraph.holdsForAny((ICPPClassType type) -> {
         *   return type.toString().equals("SomeType");
         *   }, ApplicationPolicy.SKIP_MOST_DERIVED);
         * }
         * </pre>
         *
         * @param predicate
         * The predicate to evaluate for the nodes of the inheritance
         * graph
         * @param policy
         * The policy describing how to evaluate the predicate
         * @return {@code true} iff. the predicate holds for any of the graph
         * nodes, {@code false} otherwise.
         */
        @SafeVarargs
        public final boolean holdsForAny(UnaryPredicate<ICPPClassType> predicate, ApplicationPolicy... policy) {
            ReferenceWrapper<Boolean> result = new ReferenceWrapper<>(new Boolean(false));
            fEntryPoint.apply((ICPPClassType type) -> {
                result.set(result.get() | predicate.evaluate(type));
            }, policy);
            return result.get();
        }

        @Override
        public String toString() {
            return fEntryPoint.toString();
        }

    }

    private static boolean areSameFunctionTypeIgnoringConst(ICPPFunctionType lhs, ICPPFunctionType rhs) {
        if (lhs.getParameterTypes().length != rhs.getParameterTypes().length) {
            return false;
        }

        if (lhs.hasRefQualifier() || rhs.hasRefQualifier()) {
            if (lhs.isRValueReference() != rhs.isRValueReference()) {
                return false;
            }
        }

        for (int parameterIndex = 0; parameterIndex < lhs.getParameterTypes().length; ++parameterIndex) {
            if (!Type.areSameTypeIgnoringConst(lhs.getParameterTypes()[parameterIndex], rhs.getParameterTypes()[parameterIndex])) {
                return false;
            }
        }

        return true;
    }

    private static boolean areSamePointerToMemberTypeIgnoringConst(ICPPPointerToMemberType lhs, ICPPPointerToMemberType rhs) {
        if (!lhs.getMemberOfClass().isSameType(rhs.getMemberOfClass())) {
            return false;
        }

        return Type.areSameTypeIgnoringConst(lhs.getType(), rhs.getType());
    }

    private static boolean areSamePointerTypeIgnoringConst(IPointerType lhs, IPointerType rhs) {
        if (!(Type.pointerLevels(lhs) == Type.pointerLevels(rhs))) {
            return false;
        }
        return Type.areSameTypeIgnoringConst(SemanticUtil.getUltimateType(lhs, false), SemanticUtil.getUltimateType(rhs, false));
    }

    /**
     * Check if two types are equal without checking for const compatibility
     * <p>
     * This function check for type equality while ignoring const qualification
     * at <b>ALL</b> levels.
     *
     * @param lhs
     * The left hand operand
     * @param rhs
     * The right hand operand
     * @return {@code true} iff. both type would be equal if all
     * const-qualifications would be removed from both types,
     * {@code false} otherwise.
     */
    public static boolean areSameTypeIgnoringConst(IType lhs, IType rhs) {
        if (lhs == null || rhs == null) {
            return lhs == rhs;
        }

        lhs = SemanticUtil.getSimplifiedType(lhs);
        rhs = SemanticUtil.getSimplifiedType(rhs);

        lhs = SemanticUtil.getNestedType(lhs, SemanticUtil.ALLCVQ);
        rhs = SemanticUtil.getNestedType(rhs, SemanticUtil.ALLCVQ);

        if (lhs.isSameType(rhs)) {
            return true;
        }

        if (!lhs.getClass().equals(rhs.getClass())) {
            return false;
        }

        if (lhs instanceof ICPPPointerToMemberType) {
            return Type.areSamePointerToMemberTypeIgnoringConst((ICPPPointerToMemberType) lhs, (ICPPPointerToMemberType) rhs);
        } else if (lhs instanceof IPointerType) {
            return Type.areSamePointerTypeIgnoringConst((IPointerType) lhs, (IPointerType) rhs);
        } else if (lhs instanceof ICPPReferenceType) {
            return Type.areSameTypeIgnoringConst(((ICPPReferenceType) lhs).getType(), ((ICPPReferenceType) rhs).getType());
        } else if (lhs instanceof ICPPFunctionType) {
            return Type.areSameFunctionTypeIgnoringConst((ICPPFunctionType) lhs, (ICPPFunctionType) rhs);
        }

        return false;
    }

    public static int arrayDimension(IType type) {
        int arrayDimension = 0;
        type = SemanticUtil.getSimplifiedType(type);

        while ((Type.isPointer(type) || Type.isArray(type) || Type.isReference(type)) && !(type instanceof IQualifierType)) {
            ++arrayDimension;
            type = ((ITypeContainer) type).getType();
        }
        return arrayDimension;
    }

    private static IType decay(IType type) {
        if (type instanceof ITypeContainer) {
            type = ((ITypeContainer) type).getType();
        }

        return SemanticUtil.getSimplifiedType(type);
    }

    public static boolean isArray(IType suspect) {
        return SemanticUtil.getSimplifiedType(suspect) instanceof IArrayType;
    }

    public static boolean isArrayLike(IType suspect) {
        if(isArray(suspect)) {
            return true;
        }

        IType simplifiedType = SemanticUtil.getSimplifiedType(suspect);
        final boolean isReferenceToArray = simplifiedType instanceof ICPPReferenceType && ((ICPPReferenceType) simplifiedType)
                .getType() instanceof IArrayType;
        final boolean isPointerToArray = simplifiedType instanceof IPointerType && ((IPointerType) simplifiedType)
                        .getType() instanceof IArrayType;
        return isReferenceToArray || isPointerToArray;
    }

    public static boolean isConst(IArrayType suspect) {
        IType genericAlias = suspect;
        while (genericAlias instanceof IArrayType) {
            genericAlias = ((IArrayType) genericAlias).getType();
        }

        return genericAlias instanceof IQualifierType && ((IQualifierType) genericAlias).isConst();
    }

    public static boolean isConst(IType suspect, int pointerLevel) {

        suspect = SemanticUtil.getSimplifiedType(suspect);

        for (; pointerLevel > 0; --pointerLevel) {
            if (suspect instanceof ITypeContainer) {
                suspect = SemanticUtil.getSimplifiedType(((ITypeContainer) suspect).getType());
            } else {
                return false;
            }
        }

        if (suspect instanceof IQualifierType) {
            return ((IQualifierType) suspect).isConst();
        } else if (suspect instanceof IPointerType) {
            return ((IPointerType) suspect).isConst();
        } else if (suspect instanceof ICPPFunctionType) {
            return ((ICPPFunctionType) suspect).isConst();
        } else if (suspect instanceof IArrayType) {
            return Type.isConst((IArrayType) suspect);
        }

        return false;
    }

    public static boolean isMoreConst(IType suspect, IType original) {
        suspect = Type.decay(suspect);
        original = Type.decay(original);

        boolean originalConst = Type.isConst(original, 0);

        boolean suspectConst = Type.isConst(suspect, 0);

        while (original instanceof ITypeContainer && suspect instanceof ITypeContainer) {
            if (originalConst != suspectConst) {
                return !originalConst && suspectConst;
            }

            original = Type.decay(original);
            suspect = Type.decay(suspect);

            originalConst = Type.isConst(original, 0);

            suspectConst = Type.isConst(suspect, 0);
        }

        return Type.isConst(suspect, 0) && !Type.isConst(original, 0);
    }

    public static boolean isPointer(IType suspect) {
        return SemanticUtil.getSimplifiedType(suspect) instanceof IPointerType;
    }

    public static boolean isReference(IType suspect) {
        return SemanticUtil.getSimplifiedType(suspect) instanceof ICPPReferenceType;
    }

    public static boolean isRValueReference(IType suspect) {
        IType simplifiedType = SemanticUtil.getSimplifiedType(suspect);
        return simplifiedType instanceof ICPPReferenceType && ((ICPPReferenceType) simplifiedType).isRValueReference();
    }

    public static int pointerLevels(IType type) {
        int levels = 0;
        type = SemanticUtil.getSimplifiedType(type);

        while (type instanceof ITypeContainer) {
            if (Type.isPointer(type) || Type.isReference(type)) {
                ++levels;
            }
            type = ((ITypeContainer) type).getType();
        }

        return levels;
    }

    public static boolean referencesNonPointer(IType suspect) {
        if (Type.isReference(suspect)) {
            suspect = Type.decay(suspect);
            return !(suspect instanceof IPointerType);
        }

        return false;
    }

    public static ICPPMethod[] getAssignmentOperatorsOf(ICPPClassType classType) {
        //@formatter:off
        return Stream.of(classType.getMethods())
                     .filter(mfn -> mfn.getName().equals("operator ="))
                     .toArray(ICPPMethod[]::new);
        //@formatter:on
    }

    public static ICPPMethod[] getCopyAssignmentOperatorsOf(ICPPClassType classType) {
        //@formatter:off
        return Stream.of(getAssignmentOperatorsOf(classType))
                     .filter(assop -> {
                         ICPPParameter[] parameters = assop.getParameters();
                         return parameters.length == 1 && Stream.of(parameters)
                                 .allMatch(param -> {
                                     IType type = param.getType();
                                     return (type instanceof ICPPReferenceType) && 
                                             !((ICPPReferenceType)type).isRValueReference() &&
                                             areSameTypeIgnoringConst(((ITypeContainer) type).getType(), classType);
                                 });
                     }).toArray(ICPPMethod[]::new);
        //@formatter:on
    }

}
