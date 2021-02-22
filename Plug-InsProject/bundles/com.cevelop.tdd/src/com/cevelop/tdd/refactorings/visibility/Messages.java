package com.cevelop.tdd.refactorings.visibility;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        ChangeVisibility_descWithSection;
    public static String        ChangeVisibility_descWoSection;
    public static String        ChangeVisibility_name;
    public static String        ChangeVisibilityRefactoring_0;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
