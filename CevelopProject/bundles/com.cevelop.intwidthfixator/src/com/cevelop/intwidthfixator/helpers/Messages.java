package com.cevelop.intwidthfixator.helpers;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.cevelop.intwidthfixator.helpers.messages"; //$NON-NLS-1$
    public static String        IdHelper_Exception;
    public static String        InclHelper_S_Change_Name;
    public static String        InversionHelper_Exception;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
