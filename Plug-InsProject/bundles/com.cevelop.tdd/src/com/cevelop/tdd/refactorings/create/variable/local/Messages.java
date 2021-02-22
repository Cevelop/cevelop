package com.cevelop.tdd.refactorings.create.variable.local;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateLocalVariable_descWithSection;
    public static String        CreateLocalVariable_descWoSection;
    public static String        CreateLocalVariable_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
