package com.cevelop.codeanalysator.core.markerresolution;

import java.util.List;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import com.cevelop.codeanalysator.core.CodeAnalysatorRuntime;
import com.cevelop.codeanalysator.core.guideline.Rule;
import com.cevelop.codeanalysator.core.guideline.RuleRegistry;


public class CodeAnalysatorMarkerResolutionGenerator implements IMarkerResolutionGenerator {

    private final RuleRegistry ruleRegistry;

    public CodeAnalysatorMarkerResolutionGenerator() {
        ruleRegistry = CodeAnalysatorRuntime.getDefault().getRuleRegistry();
    }

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        try {
            return tryGetResolutions(marker);
        } catch (CoreException e) {
            e.printStackTrace();
            return new IMarkerResolution[] {};
        }
    }

    private IMarkerResolution[] tryGetResolutions(IMarker marker) throws CoreException {
        Rule rule = getRuleForMarker(marker);
        List<IMarkerResolution> quickFixes = rule.getQuickFixes();
        return quickFixes.stream() //
                .filter(quickFix -> isQuickFixApplicable(marker, quickFix)) //
                .toArray(IMarkerResolution[]::new);
    }

    private Rule getRuleForMarker(IMarker marker) throws CoreException {
        String problemId = (String) marker.getAttribute(ICodanProblemMarker.ID);
        return ruleRegistry.getRuleByProblemId(problemId);
    }

    private boolean isQuickFixApplicable(IMarker marker, IMarkerResolution quickFix) {
        return !(quickFix instanceof ICodanMarkerResolution) || ((ICodanMarkerResolution) quickFix).isApplicable(marker);
    }
}
