package com.cevelop.tdd.refactorings.create.function.constructor;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateConstructor_descWithSection;
    public static String        CreateConstructor_descWoSection;
    public static String        CreateConstructor_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
