package com.cevelop.tdd.refactorings.create.function.free.operator;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {

    private static final String BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages";
    public static String        CreateFreeOperator_descWithSection;
    public static String        CreateFreeOperator_descWoSection;
    public static String        CreateFreeOperator_name;
    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
