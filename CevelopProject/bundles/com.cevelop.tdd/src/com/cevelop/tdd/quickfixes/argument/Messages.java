package com.cevelop.tdd.quickfixes.argument;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        ArgumentMismatchQuickfixGenerator_Add;
    public static String        ArgumentMismatchQuickfixGenerator_Remove;
    public static String        ArgumentMismatchQuickfixGenerator_Placeholder;
    //    public static String        ArgumentMismatchQuickfixGenerator_Argument;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
