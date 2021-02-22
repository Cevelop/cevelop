package com.cevelop.codeanalysator.core.helper;

import org.osgi.framework.FrameworkUtil;


public class CoreIdHelper {

    public static final String PLUGIN_ID         = FrameworkUtil.getBundle(CoreIdHelper.class).getSymbolicName();
    public static final String DEFAULT_QUALIFIER = PLUGIN_ID;

    /* Preference Constants */
    public static final String GUIDELINE_SETTING_ID      = DEFAULT_QUALIFIER + ".guidelineSettings";        //$NON-NLS-1$
    public static final String GUIDELINE_SETTING_PAGE_ID = DEFAULT_QUALIFIER + ".preference.guideline.page";
    public static final String GUIDELINE_EXTENSION_ID    = DEFAULT_QUALIFIER + ".guideline";

    public static final String AlwaysInitializeAnObjectSharedProblemId           = "AlwaysInitializeAnObject";
    public static final String AvoidConversionOperatorsSharedProblemId           = "AvoidConversionOperators";
    public static final String AvoidLossyConversionsSharedProblemId              = "AvoidLossyConversions";
    public static final String DeclareLoopVariableInTheIntializerSharedProblemId = "DeclareLoopVariableInTheIntializer";
    public static final String MissingSpecialMemberFunctionsSharedProblemId      = "MissingSpecialMemberFunctions";
    public static final String RedundantOperationsSharedProblemId                = "RedundantOperations";
}
