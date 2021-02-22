package com.cevelop.tdd.refactorings.create.function.free;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateFreeFunction_descWithSection;
    public static String        CreateFreeFunction_descWoSection;
    public static String        CreateFreeFunction_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
