package com.cevelop.tdd.refactorings.create.variable.member;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateMemberVariable_descWithSection;
    public static String        CreateMemberVariable_descWoSection;
    public static String        CreateMemberVariable_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
