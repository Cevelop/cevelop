package com.cevelop.ctylechecker.reporting;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.cevelop.ctylechecker.ids.IdHelper;
import com.cevelop.ctylechecker.problems.CtyleProblem;
import com.cevelop.ctylechecker.reporting.util.Pair;


public class TUReport {

    private final ITranslationUnit unit;
    private final static String    NL = System.getProperty("line.separator");

    public TUReport(final ITranslationUnit unit) {
        this.unit = unit;
    }

    public String print() {
        final List<IMarker> markers = getMarkers();
        String report = markers.stream().map(MarkerReport::getCtyleProblem).distinct().map(this::print).reduce((accu, elem) -> accu + elem).orElse(
                formatListItem("Looking good."));
        report += printFurtherInfos();
        return getUnitName() + NL + report;
    }

    private List<IMarker> getMarkers() {
        try {
            final IMarker[] markers = unit.getResource().findMarkers(IdHelper.MARKER_ID, true, IResource.DEPTH_INFINITE);
            return Arrays.asList(markers);
        } catch (final CoreException e) {
            return Collections.emptyList();
        }
    }

    private String getUnitName() {
        return unit.getFile().getName();
    }

    private String formatListItem(final String message) {
        return "\t* " + message + NL;
    }

    private String formatListIntendation(final String message) {
        return "\t  " + message + NL;
    }

    private String print(final CtyleProblem problem) {
        final StringBuilder builder = new StringBuilder();
        printProblem(problem.getProblem(), builder);
        printExplanation(problem.getExplanation(), builder);
        return builder.toString();
    }

    private String printFurtherInfos() {
        final StringBuilder builder = new StringBuilder();
        final Set<Pair<String, URL>> resources = getResources();
        if (!resources.isEmpty()) {
            builder.append(formatListItem("Additional infos:"));
            final String resourceNames = resources.stream().map(pair -> pair.getFirst()).reduce((accu, key) -> accu + "'" + key + "' ").orElse("");
            builder.append(formatListIntendation(resourceNames));
        }
        return builder.toString();
    }

    private void printExplanation(final String explanation, final StringBuilder builder) {
        Arrays.asList(explanation.split("\\r?\\n")).stream().map(this::formatListIntendation).forEach(builder::append);
    }

    private void printProblem(final String problem, final StringBuilder builder) {
        builder.append(formatListItem(problem));
    }

    public static TUReport create(final ITranslationUnit tu) {
        return new TUReport(tu);
    }

    public Set<Pair<String, URL>> getResources() {
        final Set<Pair<String, URL>> resources = new HashSet<>();
        getMarkers().stream().map(MarkerReport::getCtyleProblem).distinct().forEach(p -> p.getResources().forEach((k, v) -> {
            resources.add(new Pair<>(k, v));
        }));
        return resources;
    }

}
