package com.cevelop.codeanalysator.core.suppression;

import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.parser.util.ArrayUtil;

import com.cevelop.codeanalysator.core.util.AttributeOwnerHelper;


public class AttributeMatcher {

    private AttributeMatcher() {}

    public static boolean check(final List<? extends SuppressionAttribute> suppressionAttrs, final IASTNode node) {
        return check(suppressionAttrs, node, IASTTranslationUnit.class);
    }

    public static <T extends IASTNode> boolean check(final List<? extends SuppressionAttribute> suppressionAttrs, final IASTNode node,
            Class<T> upUntilType) {
        assert (upUntilType == null || IASTAttributeOwner.class.isAssignableFrom(upUntilType) || IASTTranslationUnit.class.equals(upUntilType));
        final IASTAttributeSpecifier[] attrs = getAttributeSpec(node, upUntilType);

        if (attrs != null && attrs.length != 0) {
            return attributeMatches(suppressionAttrs, attrs);
        }

        return false;
    }

    public static <T extends IASTNode> IASTAttributeSpecifier[] getAttributeSpec(IASTNode node, Class<T> upUntilType) {
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

    private static boolean attributeMatches(final List<? extends SuppressionAttribute> suppressionAttrs, final IASTAttributeSpecifier[] attrs) {
        for (final IASTAttributeSpecifier attr : attrs) {
            final String rawSignature = attr.getRawSignature();
            for (SuppressionAttribute suppressionAttr : suppressionAttrs) {
                if (rawSignature.contains(suppressionAttr.getScope()) && (suppressionAttr.getIgnoreText().length() == 0 || rawSignature.contains(
                        "\"" + suppressionAttr.getIgnoreText() + "\""))) {
                    return true;
                }
            }
        }
        return false;
    }
}
