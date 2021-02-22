package com.cevelop.codeanalysator.core.suppression;

import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeList;
import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAttributeList;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPNodeFactory;

import com.cevelop.codeanalysator.core.quickfix.BaseQuickFix;
import com.cevelop.codeanalysator.core.util.AttributeOwnerHelper;


@SuppressWarnings("restriction")
public class AttributeSuppressQuickFix extends BaseQuickFix {

    private CPPNodeFactory factory = CPPNodeFactory.getDefault();

    private final SuppressionAttribute attribute;

    public AttributeSuppressQuickFix(String label, SuppressionAttribute suppressionAttribute) {
        super(label);
        attribute = suppressionAttribute;
    }

    @Override
    protected void handleMarkedNode(IASTNode markedNode, ASTRewrite hRewrite) {
        String ignorestring = "\"" + attribute.getIgnoreText() + "\"";

        IASTNode oldAttrOwner = AttributeOwnerHelper.getWantedAttributeOwner(markedNode);
        IASTAttributeOwner newAttrOwner = (IASTAttributeOwner) oldAttrOwner.copy(CopyStyle.withoutLocations);

        if (newAttrOwner.getAttributeSpecifiers().length == 0) {
            newAttrOwner.addAttributeSpecifier(createNewAttributeSpecifier(ignorestring));
        } else {
            IASTAttributeList firstAttrSpec = (IASTAttributeList) newAttrOwner.getAttributeSpecifiers()[0];
            firstAttrSpec.addAttribute(createNewAttr(ignorestring));
        }

        hRewrite.replace(oldAttrOwner, newAttrOwner, null);
    }

    private IASTAttributeList createNewAttributeSpecifier(final String ruleNr) {
        final ICPPASTAttributeList newAttrList = factory.newAttributeList();
        newAttrList.addAttribute(createNewAttr(ruleNr));
        return newAttrList;
    }

    private IASTAttribute createNewAttr(final String ruleNr) {
        final IASTAttribute attr = factory.newAttribute(attribute.getScope().toCharArray(), ruleNr.length() > 0 ? factory.newToken(0, ruleNr
                .toCharArray()) : null);
        return attr;
    }
}
