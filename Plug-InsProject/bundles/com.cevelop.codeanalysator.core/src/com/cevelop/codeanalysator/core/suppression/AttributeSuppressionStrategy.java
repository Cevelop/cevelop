package com.cevelop.codeanalysator.core.suppression;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ui.IMarkerResolution;

import com.cevelop.codeanalysator.core.guideline.ISuppressionStrategy;


public class AttributeSuppressionStrategy implements ISuppressionStrategy {

    private final String scope;
    private final String guidelineSuppressLabelName;

    public AttributeSuppressionStrategy(String guidelineSuppressTag, String guidelineSuppressLabelName) {
        this.scope = String.format("%s::suppress", guidelineSuppressTag);
        this.guidelineSuppressLabelName = guidelineSuppressLabelName;
    }

    @Override
    public boolean isRuleSuppressedForNode(String ruleNr, IASTNode node) {
        ArrayList<SuppressionAttribute> attributes = new ArrayList<>();
        attributes.add(createAllSuppressionAttribute());
        attributes.add(createSuppressionAttribute(ruleNr));
        return AttributeMatcher.check(attributes, node);
    }

    @Override
    public IMarkerResolution getSuppressAllQuickFix() {
        String suppressLabel = "Suppress all guidelines for this statement";
        SuppressionAttribute allSuppressionAttribute = createAllSuppressionAttribute();
        return new AttributeSuppressQuickFix(suppressLabel, allSuppressionAttribute);
    }

    @Override
    public IMarkerResolution getSuppressRuleQuickFix(String ruleNr) {
        String suppressLabel = String.format("Suppress %s %s for this statement", guidelineSuppressLabelName, ruleNr);
        SuppressionAttribute suppressionAttribute = createSuppressionAttribute(ruleNr);
        return new AttributeSuppressQuickFix(suppressLabel, suppressionAttribute);
    }

    private SuppressionAttribute createAllSuppressionAttribute() {
        return new SuppressionAttribute("all::suppress", "");
    }

    private SuppressionAttribute createSuppressionAttribute(String ruleNr) {
        return new SuppressionAttribute(scope, ruleNr);
    }
}
