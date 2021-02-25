package com.cevelop.intwidthfixator.preferences;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.cevelop.intwidthfixator.preferences.messages"; //$NON-NLS-1$
    public static String        L_Caption;
    public static String        L_Char;
    public static String        L_Int;
    public static String        L_Long;
    public static String        L_LongLong;
    public static String        L_Short;
    public static String        L_SignedQualifier;
    public static String        L_Size16;
    public static String        L_Size32;
    public static String        L_Size64;
    public static String        L_Size8;
    public static String        L_TargetSize;
    public static String        L_Val_Signed;
    public static String        L_Val_Unsigned;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
