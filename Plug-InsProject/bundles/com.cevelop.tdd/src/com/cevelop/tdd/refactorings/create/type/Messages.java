package com.cevelop.tdd.refactorings.create.type;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateType_descWithSection;
    public static String        CreateType_descWoSection;
    public static String        CreateType_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
