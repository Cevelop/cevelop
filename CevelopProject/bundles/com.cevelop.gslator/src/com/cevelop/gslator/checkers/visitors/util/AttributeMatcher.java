package com.cevelop.gslator.checkers.visitors.util;

import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.util.ArrayUtil;

import com.cevelop.gslator.nodes.util.AttributeOwnerHelper;


public class AttributeMatcher {

    public static final String IGNORE_ATTRIBUTE = "gsl::suppress";

    private AttributeMatcher() {}

    public static boolean check(final ICheckIgnoreAttribute ignore, final IASTNode node) {
        return check(ignore, node, IASTTranslationUnit.class);
    }

    public static <T extends IASTNode> boolean check(final ICheckIgnoreAttribute ignore, final IASTNode node, Class<T> upUntilType) {
        assert (upUntilType == null || IASTAttributeOwner.class.isAssignableFrom(upUntilType) || IASTTranslationUnit.class.equals(upUntilType));
        final IASTAttributeSpecifier[] attrs = getAttributeSpec(node, upUntilType);

        if (attrs != null && attrs.length != 0) {
            return !attributeMatches(ignore, attrs);
        }
        return true;
    }

    private static <T extends IASTNode> IASTAttributeSpecifier[] getAttributeSpec(IASTNode node, Class<T> upUntilType) {
        IASTAttributeOwner attrnode = AttributeOwnerHelper.getValidAttributeOwner(node);
        if (attrnode == null) return null;
        IASTAttributeSpecifier[] attrs = attrnode.getAttributeSpecifiers();
        while (attrnode != null && !isWantedNode(attrnode, upUntilType)) {
            attrnode = AttributeOwnerHelper.getValidAttributeOwner(attrnode.getParent());
            if (attrnode != null) attrs = ArrayUtil.addAll(attrs, attrnode.getAttributeSpecifiers());
        }
        return attrs;
    }

    private static <T extends IASTNode> boolean isWantedNode(IASTAttributeOwner attrnode, Class<T> optionalTypeRestriction) {
        if (AttributeOwnerHelper.instanceOfUnwanted(attrnode)) return false;
        if (optionalTypeRestriction != null && !optionalTypeRestriction.isAssignableFrom(attrnode.getClass())) return false;
        return true;
    }

    private static boolean attributeMatches(final ICheckIgnoreAttribute ignore, final IASTAttributeSpecifier[] attrs) {
        for (final IASTAttributeSpecifier attr : attrs) {
            final String rawSignature = attr.getRawSignature();
            if (rawSignature.contains(IGNORE_ATTRIBUTE)) {
                if (ignore.getIgnoreString().length() > 0 && rawSignature.contains("\"" + ignore.getIgnoreString() + "\"")) {
                    return true;
                }
                if (ignore.getProfileGroup().length() > 0 && (rawSignature.contains("\"" + ignore.getProfileGroup() + "\"") || rawSignature.contains(
                        "\"" + ignore.getProfileGroup() + "." + ignore.getNrInProfileGroup() + "\""))) {
                    return true;
                }
            }
        }
        return false;
    }
}
