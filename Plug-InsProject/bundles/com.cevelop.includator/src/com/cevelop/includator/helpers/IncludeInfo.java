/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.helpers;

import org.eclipse.core.runtime.IPath;


public class IncludeInfo {

    private String       includeStatementString;
    private final String targetPath;
    private boolean      isSystemInclude;
    private IPath        relativePath;
    private final String absolutePath;

    public IncludeInfo(String targePath, String absolutePath) {
        this.targetPath = targePath;
        this.absolutePath = absolutePath;
    }

    public void setIncludeStatementString(String includeStatementString) {
        this.includeStatementString = includeStatementString;
    }

    public void setIsSystemInclude(boolean isSystemInclude) {
        this.isSystemInclude = isSystemInclude;
    }

    @Override
    public String toString() {
        return includeStatementString;
    }

    public String getIncludeStatementString() {
        return includeStatementString;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public boolean isSystemInclude() {
        return isSystemInclude;
    }

    public void setRelativePath(IPath relativePath) {
        this.relativePath = relativePath;
    }

    public IPath getRelativePath() {
        return relativePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
