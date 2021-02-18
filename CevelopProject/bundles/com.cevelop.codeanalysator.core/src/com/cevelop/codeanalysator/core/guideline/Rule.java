package com.cevelop.codeanalysator.core.guideline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.ui.IMarkerResolution;


public class Rule {

    private static final String STRING_TEMPLATE   = "Rule [ruleNr=%s, guideline=%s]";
    private String              ruleNr;
    private IGuideline          guideline;
    private String              problemId;
    private IMarkerResolution[] markerResolutions = new IMarkerResolution[] {};
    private boolean             isShared          = false;
    private String              sharedProblemId   = null;

    public Rule(String ruleNr, IGuideline guideline, String problemId) {
        this.ruleNr = ruleNr;
        this.guideline = guideline;
        this.problemId = problemId;
    }

    public Rule(String ruleNr, IGuideline guideline, String problemId, String sharedProblemId) {
        this(ruleNr, guideline, problemId);
        this.isShared = true;
        this.sharedProblemId = sharedProblemId;
    }

    public void addQuickFixes(IMarkerResolution... quickfixes) {
        markerResolutions = quickfixes;
    }

    public String getRuleNr() {
        return ruleNr;
    }

    public String getProblemId() {
        return problemId;
    }

    public IGuideline getGuideline() {
        return guideline;
    }

    public boolean isSharedProblem() {
        return isShared;
    }

    public String getSharedProblemId() {
        return sharedProblemId;
    }

    public boolean isSuppressedForNode(IASTNode node) {
        return getSuppressionStrategy().isRuleSuppressedForNode(ruleNr, node);
    }

    public List<IMarkerResolution> getQuickFixes() {
        List<IMarkerResolution> quickFixes = new ArrayList<>();
        quickFixes.addAll(Arrays.asList(markerResolutions));
        quickFixes.add(getSuppressAllQuickFix());
        quickFixes.add(getSuppressQuickFix());
        return quickFixes;
    }

    public IMarkerResolution getSuppressQuickFix() {
        return getSuppressionStrategy().getSuppressRuleQuickFix(ruleNr);
    }

    public IMarkerResolution getSuppressAllQuickFix() {
        return getSuppressionStrategy().getSuppressAllQuickFix();
    }

    @Override
    public String toString() {
        return String.format(STRING_TEMPLATE, ruleNr, guideline.getName());
    }

    private ISuppressionStrategy getSuppressionStrategy() {
        return guideline.getSuppressionStrategy();
    }
}
