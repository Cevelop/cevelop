package com.cevelop.intwidthfixator.refactorings;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.cevelop.intwidthfixator.refactorings.messages"; //$NON-NLS-1$

    public static String ConvRef_descWithSelection;
    public static String ConvRef_descWoSelection;
    public static String ConvRef_name;

    public static String InvRef_descWithSection;
    public static String InvRef_descWoSection;
    public static String InvRef_name;

    static {
        // initialize resource bundle
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
