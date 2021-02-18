/*******************************************************************************
 * Copyright (c) 2010 Institute for Software, HSR Hochschule fuer Technik
 * Rapperswil, University of applied sciences and others
 * All rights reserved.
 *
 * Contributors:
 * Institute for Software - initial API and implementation
 ******************************************************************************/
package com.cevelop.includator.ui;

public interface Markers {

    public static final String INCLUDATOR_MARKER                 = "com.cevelop.includator.marker";                       // adding a marker of this type will not be visible in an editor
    public static final String INCLUDATOR_PPROBLEM_MARKER        = "com.cevelop.includator.problemmarker";
    public static final String INCLUDATOR_UNUSED_INCLUDE_MARKER  = "com.cevelop.includator.unusedincludemarker";
    public static final String COVERAGE_IN_USE_MARKER            = "com.cevelop.includator.coverageInUseMarker";
    public static final String COVERAGE_IMPLICITLY_IN_USE_MARKER = "com.cevelop.includator.coverageImplicitlyInUseMarker";
    public static final String COVERAGE_NOT_IN_USE_MARKER        = "com.cevelop.includator.coverageNotInUseMarker";
    public static final String INCLUDATOR_ADD_INCLUDE_MARKER     = "com.cevelop.includator.addincludemarker";
    public static final String INCLUDATOR_UNUSED_FILE_MARKER     = "com.cevelop.includator.unusedfilemarker";
    public static final String REPLACE_INCLUDE_WITH_FWD_MARKER   = "com.cevelop.includator.includetofwdmarker";

    @Deprecated
    public static final String INCLUDATOR_UNDO_MARKER = "com.cevelop.includator.undomarker";
}
