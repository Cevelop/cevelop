package com.cevelop.codeanalysator.core.guideline;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ui.IMarkerResolution;


public interface ISuppressionStrategy {

    boolean isRuleSuppressedForNode(String ruleNr, IASTNode node);

    IMarkerResolution getSuppressAllQuickFix();

    IMarkerResolution getSuppressRuleQuickFix(String ruleNr);
}
