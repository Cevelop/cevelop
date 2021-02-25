package com.cevelop.includator.optimizer.organizeincludes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.cevelop.includator.helpers.FileHelper;
import com.cevelop.includator.optimizer.directlyincludereferenceddeclaration.AddIncludeSuggestion;
import com.cevelop.includator.resources.IncludatorFile;


/**
 * Contains information if suggestion <code>suggestion</code> covers (or "contains") what other suggestions (<code>coveredSuggestions</code>) would
 * suggest.
 */
public class OtherFileCoverageData {

    private final AddIncludeSuggestion             suggestion;
    private final IncludatorFile                   fileToInclude;
    private final ArrayList<OtherFileCoverageData> coveredSuggestions;
    private final IncludatorFile                   requiredDeclarationFile;
    private long                                   fileToIncludeWeight;

    public OtherFileCoverageData(AddIncludeSuggestion suggestion, IncludatorFile fileToInclude, IncludatorFile requiredDeclarationFile) {
        this.suggestion = suggestion;
        this.fileToInclude = fileToInclude;
        this.requiredDeclarationFile = requiredDeclarationFile;
        coveredSuggestions = new ArrayList<>();
        fileToIncludeWeight = -1;
    }

    public AddIncludeSuggestion getSuggestion() {
        return suggestion;
    }

    public IncludatorFile getFileToInclude() {
        return fileToInclude;
    }

    public IncludatorFile getRequiredDeclarationFile() {
        return requiredDeclarationFile;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OtherFileCoverageData)) {
            return false;
        }
        OtherFileCoverageData otherData = (OtherFileCoverageData) obj;
        return suggestion.equals(otherData.suggestion);
    }

    public void addCovered(OtherFileCoverageData coveredSuggestion) {
        coveredSuggestions.add(coveredSuggestion);
    }

    public List<OtherFileCoverageData> getCoveredSuggestions() {
        return coveredSuggestions;
    }

    @Override
    public String toString() {
        return suggestion.toString() + " covers " + coveredSuggestions.size() + " other includes.";
    }

    public long getFileToIncludeWeight() {
        if (fileToIncludeWeight == -1) {
            fileToIncludeWeight = FileHelper.getWeight(getFileToInclude());
        }
        return fileToIncludeWeight;
    }
}



class IncludeCoverageDataComperator implements Comparator<OtherFileCoverageData> {

    @Override
    public int compare(OtherFileCoverageData first, OtherFileCoverageData second) {
        int sizeDiff = second.getCoveredSuggestions().size() - first.getCoveredSuggestions().size();
        if (sizeDiff != 0) {
            return sizeDiff;
        }
        return (int) (first.getFileToIncludeWeight() - second.getFileToIncludeWeight());
    }
}
