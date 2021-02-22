package com.cevelop.tdd.refactorings.create.function.member;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateMemberFunction_descWithSection;
    public static String        CreateMemberFunction_descWoSection;
    public static String        CreateMemberFunction_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
