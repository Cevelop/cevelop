package com.cevelop.ctylechecker.reporting;

import java.net.URL;
import java.util.Collections;

import org.eclipse.cdt.codan.core.model.ICodanProblemMarker;
import org.eclipse.core.resources.IMarker;

import com.cevelop.ctylechecker.problems.CtyleProblem;
import com.cevelop.ctylechecker.problems.ProblemFileLoader;


public class MarkerReport {

    public static CtyleProblem getCtyleProblem(final IMarker marker) {
        final String problemId = marker.getAttribute(ICodanProblemMarker.ID, "");
        if (!problemId.isEmpty()) {
            final URL problemsFolder = ProblemFileLoader.class.getResource("/problems");
            final ProblemFileLoader problemLoader = new ProblemFileLoader(problemsFolder);
            if (problemLoader.canLoad(problemId)) {
                return problemLoader.load(problemId);
            }
        }
        return new CtyleProblem(getMessage(marker), "", Collections.emptyMap());
    }

    private static String getMessage(final IMarker marker) {
        return marker.getAttribute(IMarker.MESSAGE, "");
    }

}
