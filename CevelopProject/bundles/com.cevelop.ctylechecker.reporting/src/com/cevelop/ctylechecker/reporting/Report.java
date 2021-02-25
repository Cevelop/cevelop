package com.cevelop.ctylechecker.reporting;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.model.ITranslationUnit;

import com.cevelop.ctylechecker.reporting.util.Pair;


public class Report {

    private final List<ITranslationUnit> files;
    private final Set<Pair<String, URL>> resources;

    public Report(final List<ITranslationUnit> files) {
        this.files = files;
        resources = new HashSet<>();
    }

    public String print() {
        final String problems = files.stream().map(TUReport::create).map(this::print).reduce((accu, elem) -> accu + elem).orElse("");
        final String resources = getResources();
        return problems + "\n\n" + resources;
    }

    private String getResources() {
        if (resources.isEmpty()) {
            return "";
        }
        return "Resources:\n" + resources.stream().map(p -> "\t " + p.getFirst() + ": " + p.getSecond().toExternalForm() + "\n").reduce((accu,
                elem) -> accu + elem).orElse("");
    }

    private String print(final TUReport report) {
        resources.addAll(report.getResources());
        return report.print() + "\n";
    }

}
