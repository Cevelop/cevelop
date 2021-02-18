package com.cevelop.tdd.refactorings.create.function.member.operator;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateMemberOperator_descWithSection;
    public static String        CreateMemberOperator_descWoSection;
    public static String        CreateMemberOperator_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
