package com.cevelop.includator.helpers;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.cevelop.includator.IncludatorPlugin;
import com.cevelop.includator.dependency.DeclarationReferenceDependency;
import com.cevelop.includator.resources.IncludatorFile;


public class DeclarationPicker {

    private static final RatedDeclaration INITIAL_RATED_DECL = new RatedDeclaration();

    public static DeclarationReferenceDependency pickBestFromDeclarations(List<DeclarationReferenceDependency> declarations) {
        RatedDeclaration best = INITIAL_RATED_DECL;
        for (DeclarationReferenceDependency newDecl : declarations) {
            try {
                RatedDeclaration newRated = new RatedDeclaration(newDecl);
                if (newRated.isBetterThan(best)) {
                    best = newRated;
                }
            } catch (CoreException e) {
                String msg = "Exception while picking candidate from several declarations for " + newDecl.getDeclarationReference() + ".";
                IncludatorPlugin.logStatus(new IncludatorStatus(msg), newDecl.getDeclaration().getFile());
                continue;
            }
        }
        return best.declarationDependency;
    }
}



class RatedDeclaration {

    private static final int CATEGORY_IN_SAME_FILE          = 1;
    private static final int CATEGORY_HEADER_IN_PROJECT     = 2;
    private static final int CATEGORY_HEADER_NOT_IN_PROJECT = 3;
    private static final int CATEGORY_SOURCE_FILE           = 4;
    private static final int CATEGORY_UNKNOWN               = 5;

    private static final int UNINITIALIZED_INCLUDE_WEIGHT          = -1;
    private static final int UNINITIALIZED_RELATIVE_PATH_SEG_COUNT = -1;

    DeclarationReferenceDependency declarationDependency;
    private IncludatorFile         declFile;
    private long                   includeWeight            = UNINITIALIZED_INCLUDE_WEIGHT;
    private long                   relativePathSegmentCount = UNINITIALIZED_RELATIVE_PATH_SEG_COUNT;
    int                            category;

    public RatedDeclaration(DeclarationReferenceDependency declarationDependency) {
        this.declarationDependency = declarationDependency;
        declFile = declarationDependency.getDeclaration().getFile();
        initCategory();
    }

    public RatedDeclaration() {
        category = CATEGORY_UNKNOWN;
    }

    private void initCategory() {
        if (declarationDependency.isLocalDependency()) {
            category = CATEGORY_IN_SAME_FILE;
        } else if (!declFile.isPartOfProject()) {
            category = CATEGORY_HEADER_NOT_IN_PROJECT;
        } else if (declFile.isHeaderFile()) {
            category = CATEGORY_HEADER_IN_PROJECT;
        } else {
            category = CATEGORY_SOURCE_FILE;
        }
    }

    private long getIncludeWeight() throws CoreException {
        if (includeWeight == UNINITIALIZED_INCLUDE_WEIGHT) {
            initIncludeWeight();
        }
        return includeWeight;
    }

    private void initIncludeWeight() throws CoreException {
        long targetFileWeight = FileHelper.getWeight(declFile);
        includeWeight = getRelativePathSegmentCount() * getRelativePathSegmentCount() * targetFileWeight;
    }

    private long getRelativePathSegmentCount() {
        if (relativePathSegmentCount == UNINITIALIZED_RELATIVE_PATH_SEG_COUNT) {
            initRelativePathSegmentCount();
        }
        return relativePathSegmentCount;
    }

    private void initRelativePathSegmentCount() {
        IPath originPath = FileHelper.stringToPath(declarationDependency.getDeclarationReference().getFile().getFilePath());
        IPath destinationPath = FileHelper.stringToPath(declFile.getFilePath());
        relativePathSegmentCount = destinationPath.makeRelativeTo(originPath.removeLastSegments(1)).segmentCount();
    }

    public boolean isBetterThan(RatedDeclaration other) throws CoreException {
        if (category < other.category) {
            return true;
        }
        if (getIncludeWeight() < other.getIncludeWeight()) {
            return true;
        } else if (getIncludeWeight() == other.getIncludeWeight()) {
            boolean isThisNameSmaller = declFile.getFilePath().compareTo(other.declFile.getFilePath()) < 0;
            return isThisNameSmaller;
        }
        return false;
    }
}
