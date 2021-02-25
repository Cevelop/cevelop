package com.cevelop.constificator.core.deciders.rules;

import static com.cevelop.constificator.core.util.semantic.Type.getCopyAssignmentOperatorsOf;

import java.util.stream.Stream;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPField;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.TypeTraits;

import com.cevelop.constificator.core.util.ast.ASTRewriteCache;
import com.cevelop.constificator.core.util.structural.Node;
import com.cevelop.constificator.core.util.structural.Relation;
import com.cevelop.constificator.core.util.type.Cast;


@SuppressWarnings("restriction")
public class MemberVariables {

    /**
     * Check if a non-static data member is uninitialized.
     * <p>
     * A non-static data member is considered uninitialized if its owning type
     * is not an aggregate and all of the following conditions hold:
     * </p>
     * <ul>
     * <li>The non-static data member is not initialized by default member
     * initialization.</li>
     * <li>The owning type has at least one constructor that does not initialize
     * the non-static data member in its mem-initializer-list.</li>
     * </ul>
     *
     * @param name
     * Name of the non-static data member
     * @param cache
     * The cached ASTRewrite {@link ASTRewriteCache}
     * @return {@code true} iff the member is not initialized
     */
    public static boolean isUninitialized(ICPPASTName name, ASTRewriteCache cache) {
        ICPPClassType owner = Cast.as(ICPPClassType.class, name.getBinding().getOwner());
        if (owner == null) {
            return false;
        }

        boolean isAggregate = TypeTraits.isAggregateClass(owner);

        ICPPASTDeclarator declarator = Relation.getAncestorOf(ICPPASTDeclarator.class, name);
        boolean defaultMemberInitialized = declarator != null && declarator.getInitializer() != null;

        boolean memberListInitialized = Node.anyOfDescendingFrom(ICPPASTConstructorChainInitializer.class, name, (
                ICPPASTConstructorChainInitializer initializer, IASTName reference) -> {
            return reference.getParent().equals(initializer);
        }, cache);

        return isAggregate ? false : !(memberListInitialized || defaultMemberInitialized);
    }

    public static boolean ownerTypeCanBeCopyAssigned(ICPPASTName name, ASTRewriteCache cache) {
        ICPPClassType owner = Cast.as(ICPPClassType.class, name.getBinding().getOwner());
        if (owner == null) {
            return false;
        }

        return Stream.of(getCopyAssignmentOperatorsOf(owner)).anyMatch(assop -> !assop.isDeleted());
    }

    public static ICPPField[] memberVariablesForOwnerOf(ICPPMethod method) {
        if (method == null) {
            return new ICPPField[] {};
        }
        ICPPClassType cls = method.getClassOwner();
        IField[] fields = cls instanceof ICPPClassSpecialization ? ((ICPPClassSpecialization) cls).getFields() : cls.getFields();
        ICPPField[] cppFields = new ICPPField[fields.length];

        for (int i = 0; i < fields.length; i++) {
            if (fields[i] instanceof ICPPField) {
                cppFields[i] = (ICPPField) fields[i];
            }
        }

        return cppFields;
    }
}
