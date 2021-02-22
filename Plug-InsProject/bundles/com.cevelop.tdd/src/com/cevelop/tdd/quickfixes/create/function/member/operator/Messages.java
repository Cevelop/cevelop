package com.cevelop.tdd.quickfixes.create.function.member.operator;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateMemberOperatorQuickfixLabel;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
