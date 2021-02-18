package com.cevelop.tdd.refactorings.create.namespace;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateNamespace_descWithSection;
    public static String        CreateNamespace_descWoSection;
    public static String        CreateNamespace_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
