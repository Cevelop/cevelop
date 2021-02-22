/*
 * To avoid a Plugin Dependency Files with this header are copied from the Eclipse CUTE
 * Plugin "CharWars". In order to avoid unnecessary Class Dependencies some lines may be
 * commented out and additionally the package (and packages of other imported "CharWars"
 * Classes) got renamed from ch.hsr.ifs.cute.charwars to ch.hsr.ifs.cute.ccglator.charwarsstub
 * Some needed Adapter-Classes are found in ch.hsr.ifs.cute.ccglator.charwarsstub.stubadapter
 * and are not from the Plugin "CharWars" but created to further reduce the amount of copied files.
 */
package com.cevelop.gslator.charwarsstub.constants;

public final class PreferenceDefaults {

    public static final boolean GENERATE_GSL_PROJECT = true;
    public static final String  GSL_PROJECT_NAME     = "gsl";

    public static final boolean GENERATE_GSL_INCLUDE = true;
    public static final String  GSL_INCLUDE_PATH     = "gslrefactor.h";

    public static final String OWNER_TYPE_NAME     = "gsl::owner";
    public static final String BORROWER_TYPE_NAME  = "gsl::borrower";
    public static final String NOTNULL_TYPE_NAME   = "gsl::not_null";
    public static final String SPAN_TYPE_NAME      = "gsl::span";
    public static final String SPAN_SIZE_TYPE_NAME = "size_t std::size_t";
}
